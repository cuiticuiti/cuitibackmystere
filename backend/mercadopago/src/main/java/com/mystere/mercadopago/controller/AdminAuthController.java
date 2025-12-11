package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.model.AdminUser;
import com.mystere.mercadopago.repository.AdminUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminAuthController {

    private final AdminUserRepository adminRepo;

    public AdminAuthController(AdminUserRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AdminUser admin = adminRepo.findByUsername(request.username());

        if (admin != null && admin.getPassword().equals(request.password())) {
            return ResponseEntity.ok(Map.of("auth", true));
        }

        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}
