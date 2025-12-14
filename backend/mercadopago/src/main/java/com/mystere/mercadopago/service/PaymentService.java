package com.mystere.mercadopago.service;

import com.mystere.mercadopago.controller.PaymentRequest;
import com.mystere.mercadopago.controller.PreferenceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accessToken;

    private final RestTemplate rest = new RestTemplate();

    public PreferenceResponse createPreference(PaymentRequest request) {

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

    return new PreferenceResponse(id, initPoint);
}

}
