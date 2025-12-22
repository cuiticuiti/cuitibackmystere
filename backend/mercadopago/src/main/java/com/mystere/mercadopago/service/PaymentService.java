package com.mystere.mercadopago.service;

import com.mystere.mercadopago.controller.PaymentRequest;
import com.mystere.mercadopago.controller.PreferenceResponse;
import com.mystere.mercadopago.model.CodigoDescuento;
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

        Map response = rest.postForObject(url, body, Map.class);

        String id = (String) response.get("id");
        String initPoint = response.get("init_point").toString();

        // ===============================
        // GUARDAR PEDIDO
        // ===============================
      
        int total = request.items().stream()
        .filter(i -> i.price() != null && i.quantity() != null)
        .mapToInt(i -> i.price() * i.quantity())
        .sum();

        try {
    Pedido pedido = new Pedido();
    pedido.setFecha(LocalDateTime.now());
    pedido.setMetodoPago("MERCADO_PAGO");
    pedido.setEstado("PENDIENTE");

    pedido.setTotal(total);

    pedido.setDetalle(
            request.items().stream()
                    .map(i -> i.title() + " x" + i.quantity())
                    .toList()
                    .toString()
    );

    if (request.codigoDescuento() != null && !request.codigoDescuento().isBlank()) {
        pedido.setCodigoDescuento(request.codigoDescuento().toUpperCase());
    }

    pedidoRepo.save(pedido);

} catch (Exception e) {
    System.err.println("ERROR guardando pedido: " + e.getMessage());
}



    

        return new PreferenceResponse(id, initPoint);
    }
}
