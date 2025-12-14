package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<PreferenceResponse> create(@RequestBody PaymentRequest request) {
        PreferenceResponse pref = paymentService.createPreference(request);
        return ResponseEntity.ok(pref);
    }
}
