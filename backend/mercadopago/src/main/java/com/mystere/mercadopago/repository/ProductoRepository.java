package com.mystere.mercadopago.repository;

import com.mystere.mercadopago.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
