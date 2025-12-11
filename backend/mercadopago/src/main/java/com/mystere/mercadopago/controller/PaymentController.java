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
    public ResponseEntity<PreferenceResponse> create(@RequestBody PaymentRequest request) {
        PreferenceResponse pref = paymentService.createPreference(request);
        return ResponseEntity.ok(pref);
    }
    @GetMapping("/success")
    public ResponseEntity<Void> success(@RequestParam Map<String, String> params) {

        String telefono = "2615161952";

        String mensaje = "Nuevo pedido Mystère:%0A"
                + "ID MercadoPago: " + params.get("preference_id") + "%0A"
                + "Estado: " + params.get("status") + "%0A";

        String url = "https://wa.me/" + telefono + "?text=" + mensaje;

        return ResponseEntity.status(302).header("Location", url).build();
    }

}
