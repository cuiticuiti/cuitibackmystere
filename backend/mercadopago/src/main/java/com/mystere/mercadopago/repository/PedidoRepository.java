package com.mystere.mercadopago.repository;

import com.mystere.mercadopago.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}
