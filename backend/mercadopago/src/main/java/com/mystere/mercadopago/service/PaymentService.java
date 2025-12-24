package com.mystere.mercadopago.service;

import com.mystere.mercadopago.controller.PaymentRequest;
import com.mystere.mercadopago.controller.PreferenceResponse;
import com.mystere.mercadopago.model.Pedido;
import com.mystere.mercadopago.repository.CodigoDescuentoRepository;
import com.mystere.mercadopago.repository.PedidoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accessToken;

    private final RestTemplate rest = new RestTemplate();

    private final CodigoDescuentoRepository codigoRepo;
    private final PedidoRepository pedidoRepo;

    public PaymentService(
            CodigoDescuentoRepository codigoRepo,
            PedidoRepository pedidoRepo
    ) {
        this.codigoRepo = codigoRepo;
        this.pedidoRepo = pedidoRepo;
    }

    public PreferenceResponse createPreference(PaymentRequest request) {

        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new RuntimeException("Pedido sin items");
        }

        // 🔥 1️⃣ CALCULAR DESCUENTO (SI HAY CUPÓN)
        double descuento = 0.0;

        if (request.codigoDescuento() != null && !request.codigoDescuento().isBlank()) {
            var codigo = codigoRepo.findByCodigo(request.codigoDescuento()).orElse(null);
            if (codigo != null && codigo.getUsosDisponibles() > 0) {
                descuento = codigo.getPorcentaje() / 100.0;
            }
        }

        String url = "https://api.mercadopago.com/checkout/preferences";

        // 🔥 2️⃣ CREAR ITEMS CON PRECIO YA DESCONTADO
        List<Map<String, Object>> mpItems = request.items().stream()
                .map(item -> {
                    Map<String, Object> m = new HashMap<>();

                    double precioFinal = item.price();
                    if (descuento > 0) {
                        precioFinal = precioFinal * (1 - descuento);
                    }

                    m.put("title", item.title());
                    m.put("quantity", item.quantity());
                    m.put("currency_id", "ARS");
                    m.put("unit_price", Math.round(precioFinal)); // MP seguro

                    return m;
                })
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("items", mpItems);

        Map<String, Object> backUrls = new HashMap<>();
        backUrls.put("success", "https://mysterefragancias.com/success.html");
        backUrls.put("failure", "https://mysterefragancias.com/failure.html");
        backUrls.put("pending", "https://mysterefragancias.com/pending.html");

        body.put("back_urls", backUrls);
        body.put("auto_return", "approved");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = rest.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        System.out.println("STATUS MP = " + response.getStatusCode());
        System.out.println("RESPUESTA MP = " + response.getBody());

        Map resBody = response.getBody();

        if (resBody == null) {
            throw new RuntimeException("Respuesta vacía de Mercado Pago");
        }

        Object initPointObj =
                resBody.get("init_point") != null
                        ? resBody.get("init_point")
                        : resBody.get("punto_de_inicio");

        if (initPointObj == null) {
            throw new RuntimeException("Mercado Pago no devolvió initPoint");
        }

        String initPoint = initPointObj.toString();
        String id = resBody.get("id").toString();

        // 🔥 3️⃣ GUARDAR PEDIDO (TOTAL CON DESCUENTO)
        int total = request.items().stream()
                .mapToInt(i -> i.price() * i.quantity())
                .sum();

        if (descuento > 0) {
            total = (int) Math.round(total * (1 - descuento));
        }

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setMetodoPago("MERCADO_PAGO");
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(total);
        pedido.setDetalle(request.items().toString());

        pedidoRepo.save(pedido);

        // 🔥 4️⃣ DESCONTAR USO DEL CUPÓN (SI APLICÓ)
        if (descuento > 0 && request.codigoDescuento() != null) {
            codigoRepo.findByCodigo(request.codigoDescuento()).ifPresent(c -> {
                c.setUsosDisponibles(c.getUsosDisponibles() - 1);
                codigoRepo.save(c);
            });
        }

        return new PreferenceResponse(id, initPoint);
    }
}
