package com.mystere.mercadopago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminAuthController {

    // 🔐 credenciales fijas (por ahora)
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (
            ADMIN_USER.equals(request.username()) &&
            ADMIN_PASS.equals(request.password())
        ) {
            return ResponseEntity.ok(Map.of("auth", true));
        }

        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}
