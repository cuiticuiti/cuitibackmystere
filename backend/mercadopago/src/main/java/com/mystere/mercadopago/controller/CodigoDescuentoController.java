package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.model.CodigoDescuento;
import com.mystere.mercadopago.repository.CodigoDescuentoRepository;
import com.mystere.mercadopago.service.CodigoDescuentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/descuentos")
@CrossOrigin("*")
public class CodigoDescuentoController {

    private final CodigoDescuentoRepository repo;

    public CodigoDescuentoController(CodigoDescuentoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<CodigoDescuento> listar() {
        return repo.findAll();
    }

    @PostMapping("/validar")
    public Map<String, Object> validar(@RequestBody Map<String, String> body) {

        String codigo = body.get("codigo");

        CodigoDescuento c = repo.findByCodigo(codigo)
                .orElse(null);

        if (c == null || !c.disponible()) {
            return Map.of("valido", false);
        }

        // consumir un uso
        c.setUsosActuales(c.getUsosActuales() + 1);
        repo.save(c);

        return Map.of(
                "valido", true,
                "porcentaje", c.getPorcentaje()
        );
    }
}
