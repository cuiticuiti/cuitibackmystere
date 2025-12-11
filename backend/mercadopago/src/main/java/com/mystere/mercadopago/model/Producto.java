package com.mystere.mercadopago.model;

import jakarta.persistence.*;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Integer precio;

    private Integer precioAntes = null;   // Nuevo

    private Integer stock;

    private String imagen;

    private String genero = "unisex";         // Nuevo: hombre / mujer / unisex

    @Column(nullable = false)
    private boolean sale = false;         // Nuevo: true/false

    // ====== GETTERS & SETTERS ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public Integer getPrecioAntes() { return precioAntes; }
    public void setPrecioAntes(Integer precioAntes) { this.precioAntes = precioAntes; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public boolean isSale() { return sale; }
    public void setSale(boolean sale) { this.sale = sale; }
}

