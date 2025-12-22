package com.mystere.mercadopago.service;

import com.mystere.mercadopago.controller.PaymentRequest;
import com.mystere.mercadopago.controller.PreferenceResponse;
import com.mystere.mercadopago.model.Pedido;
import com.mystere.mercadopago.repository.CodigoDescuentoRepository;
import com.mystere.mercadopago.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        // ===============================
        // VALIDACIONES
        // ===============================
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new RuntimeException("El pedido no tiene items");
        }

        // 🔥 NUNCA null
        String codigo = request.codigoDescuento();
        if (codigo == null) {
            codigo = "";
        }

        // ===============================
        // MERCADO PAGO
        // ===============================
        String url = "https://api.mercadopago.com/checkout/preferences?access_token=" + accessToken;

        List<Map<String, Object>> mpItems = request.items().stream()
                .map(item -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("title", item.title());
                    m.put("quantity", item.quantity());
                    m.put("currency_id", "ARS");
                    m.put("unit_price", item.price());
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

        Map response;
        try {
            response = rest.postForObject(url, body, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error Mercado Pago", e);
        }

        String id = response.get("id").toString();
        String initPoint = response.get("init_point").toString();

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

        pedido.setTotal(total);
        pedido.setDetalle(request.items().toString());

        if (!codigo.isBlank()) {
            pedido.setCodigoDescuento(codigo.toUpperCase());
        }

        pedidoRepo.save(pedido);

        return new PreferenceResponse(id, initPoint);
    }
}
