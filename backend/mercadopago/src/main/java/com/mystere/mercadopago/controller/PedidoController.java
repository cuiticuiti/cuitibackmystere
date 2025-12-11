package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.model.Pedido;
import com.mystere.mercadopago.repository.PedidoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pedidos")
@CrossOrigin("*")
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @PutMapping("/{id}/estado")
    public Pedido actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        Pedido p = pedidoRepository.findById(id).orElseThrow();
        p.setEstado(estado);
        return pedidoRepository.save(p);
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
    }
}
