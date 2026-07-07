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

    private String genero = "unisex";   
    
    private String tipo;
    // Nuevo: hombre / mujer / unisex

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
    private String categoria = "PERFUME";

private String estado;       // Sellado o Usado Premium

private String capacidad;    // 128 GB, 256 GB...

private String color;

private Integer bateria;      // 100, 95, 87...

private String garantia;
public String getCategoria() {
    return categoria;
}

public void setCategoria(String categoria) {
    this.categoria = categoria;
}

public String getEstado() {
    return estado;
}

public void setEstado(String estado) {
    this.estado = estado;
}

public String getCapacidad() {
    return capacidad;
}

public void setCapacidad(String capacidad) {
    this.capacidad = capacidad;
}

public String getColor() {
    return color;
}

public void setColor(String color) {
    this.color = color;
}

public Integer getBateria() {
    return bateria;
}

public void setBateria(Integer bateria) {
    this.bateria = bateria;
}

public String getGarantia() {
    return garantia;
}

public void setGarantia(String garantia) {
    this.garantia = garantia;
}
public String getTipo() {
    return tipo;
}

public void setTipo(String tipo) {
    this.tipo = tipo;
}
}

