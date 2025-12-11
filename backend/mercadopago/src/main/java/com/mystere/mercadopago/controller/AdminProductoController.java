package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.model.Producto;
import com.mystere.mercadopago.repository.ProductoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/productos")
@CrossOrigin("*")
public class AdminProductoController {

    private final ProductoRepository repo;

    public AdminProductoController(ProductoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Producto> listar() {
        return repo.findAll();
    }

    @PostMapping
    public Producto crear(@RequestBody Producto p) {
        return repo.save(p);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto p) {
        return repo.findById(id)
                .map(existente -> {
                    existente.setNombre(p.getNombre());
                    existente.setPrecio(p.getPrecio());
                    existente.setPrecioAntes(p.getPrecioAntes());
                    existente.setStock(p.getStock());
                    existente.setImagen(p.getImagen());
                    existente.setSale(p.isSale());
                    return repo.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        repo.deleteById(id);
    }
    public record ProductoDTO(
            String nombre,
            Integer precio,
            Integer precioAntes,
            Integer stock,
            String imagen,
            String genero,
            Boolean sale
    ) {}

}
