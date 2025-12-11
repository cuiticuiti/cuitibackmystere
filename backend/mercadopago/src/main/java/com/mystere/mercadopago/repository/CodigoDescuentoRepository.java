package com.mystere.mercadopago.repository;

import com.mystere.mercadopago.model.CodigoDescuento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoDescuentoRepository extends JpaRepository<CodigoDescuento, Long> {
    Optional<CodigoDescuento> findByCodigo(String codigo);
}
