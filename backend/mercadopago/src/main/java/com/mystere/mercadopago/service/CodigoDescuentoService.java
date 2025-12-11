package com.mystere.mercadopago.service;

import com.mystere.mercadopago.model.CodigoDescuento;
import com.mystere.mercadopago.repository.CodigoDescuentoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CodigoDescuentoService {

    private final CodigoDescuentoRepository repo;

    public CodigoDescuentoService(CodigoDescuentoRepository repo) {
        this.repo = repo;
    }

    public Map<String, Object> validar(String codigo) {

        CodigoDescuento c = repo.findByCodigo(codigo).orElse(null);

        if (c == null) {
            return Map.of("valido", false, "motivo", "Código no encontrado");
        }

        if (c.getUsosActuales() >= c.getUsosMaximos()) {
            return Map.of("valido", false, "motivo", "Se alcanzó el límite de usos");
        }

        return Map.of(
                "valido", true,
                "porcentaje", c.getPorcentaje()
        );
    }

    public void registrarUso(String codigo) {
        repo.findByCodigo(codigo).ifPresent(c -> {
            c.setUsosActuales(c.getUsosActuales() + 1);
            repo.save(c);
        });
    }
}
