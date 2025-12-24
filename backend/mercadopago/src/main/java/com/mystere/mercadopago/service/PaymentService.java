package com.mystere.mercadopago.service;

import com.mystere.mercadopago.controller.PaymentRequest;
import com.mystere.mercadopago.controller.PreferenceResponse;
import com.mystere.mercadopago.model.Pedido;
import com.mystere.mercadopago.model.CodigoDescuento;
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
import java.util.Optional;

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

        // ===============================
        // DESCUENTO (SEGURO Y SIMPLE)
        // ===============================
        double descuento = 0.0;

        if (request.codigoDescuento() != null && !request.codigoDescuento().isBlank()) {
            Optional<CodigoDescuento> codOpt =
                    codigoRepo.findByCodigo(request.codigoDescuento());

            if (codOpt.isPresent()) {
                descuento = codOpt.get().getPorcentaje() / 100.0;
            }
        }

        final double descuentoFinal = descuento; // 👈 CLAVE para streams

        // ===============================
        // ITEMS CON PRECIO YA DESCONTADO
        // ===============================
        List<Map<String, Object>> mpItems = request.items().stream()
                .map(item -> {
                    Map<String, Object> m = new HashMap<>();

                    double precioFinal = item.price();
                    if (descuentoFinal > 0) {
                        precioFinal = precioFinal * (1 - descuentoFinal);
                    }

                    m.put("title", item.title());
                    m.put("quantity", item.quantity());
                    m.put("currency_id", "ARS");
                    m.put("unit_price", Math.round(precioFinal));

                    return m;
                })
                .toList();

        // ===============================
        // BODY MP
        // ===============================
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
                "https://api.mercadopago.com/checkout/preferences",
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map resBody = response.getBody();
        if (resBody == null || resBody.get("init_point") == null) {
            throw new RuntimeException("Mercado Pago no devolvió init_point");
        }

        String initPoint = resBody.get("init_point").toString();
        String id = resBody.get("id").toString();

        // ===============================
        // GUARDAR PEDIDO
        // ===============================
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setMetodoPago("MERCADO_PAGO");
        pedido.setEstado("PENDIENTE");

        int total = request.items().stream()
                .mapToInt(i -> i.price() * i.quantity())
                .sum();

        if (descuentoFinal > 0) {
            total = (int) Math.round(total * (1 - descuentoFinal));
        }

        pedido.setTotal(total);
        pedido.setDetalle(request.items().toString());
        pedidoRepo.save(pedido);

        return new PreferenceResponse(id, initPoint);
    }
}
