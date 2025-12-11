package com.mystere.mercadopago.repository;

import com.mystere.mercadopago.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    AdminUser findByUsername(String username);
}
