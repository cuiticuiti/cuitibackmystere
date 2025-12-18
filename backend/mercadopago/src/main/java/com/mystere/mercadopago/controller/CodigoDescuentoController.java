package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.model.CodigoDescuento;
import com.mystere.mercadopago.repository.CodigoDescuentoRepository;
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

    // =========================
    // LISTAR (ADMIN)
    // =========================
    @GetMapping
    public List<CodigoDescuento> listar() {
        return repo.findAll();
    }

    // =========================
    // CREAR (ADMIN)
    // =========================
    @PostMapping
    public CodigoDescuento crear(@RequestBody CodigoDescuento c) {
        c.setCodigo(c.getCodigo().toUpperCase());
        c.setUsosActuales(0);
        return repo.save(c);
    }

    // =========================
    // EDITAR (ADMIN)
    // =========================
    @PutMapping("/{id}")
    public CodigoDescuento editar(@PathVariable Long id,
                                  @RequestBody CodigoDescuento data) {

        CodigoDescuento c = repo.findById(id).orElseThrow();

        c.setCodigo(data.getCodigo().toUpperCase());
        c.setPorcentaje(data.getPorcentaje());
        c.setUsosMaximos(data.getUsosMaximos());
        c.setUsosActuales(data.getUsosActuales());

        return repo.save(c);
    }

    // =========================
    // BORRAR (ADMIN)
    // =========================
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // =========================
    // VALIDAR CUPÓN (CLIENTE)
    // =========================
    @PostMapping("/validar")
    public Map<String, Object> validar(@RequestBody Map<String, String> body) {

        String codigo = body.get("codigo").toUpperCase();

        CodigoDescuento c = repo.findByCodigo(codigo).orElse(null);

        if (c == null || !c.disponible()) {
            return Map.of("valido", false);
        }

        return Map.of(
                "valido", true,
                "porcentaje", c.getPorcentaje()
        );
    }

    // =========================
    // USAR CUPÓN (CUANDO SE PAGA)
    // =========================
    @PostMapping("/usar")
    public void usar(@RequestBody Map<String, String> body) {

        String codigo = body.get("codigo").toUpperCase();

        CodigoDescuento c = repo.findByCodigo(codigo).orElseThrow();

        if (!c.disponible()) {
            throw new RuntimeException("Cupón agotado");
        }

        c.setUsosActuales(c.getUsosActuales() + 1);
        repo.save(c);
    }
}
