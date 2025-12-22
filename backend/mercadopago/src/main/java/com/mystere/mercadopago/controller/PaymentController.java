package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pay")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PaymentRequest request) {
        try {
            PreferenceResponse pref = paymentService.createPreference(request);
            return ResponseEntity.ok(pref);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(400)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
