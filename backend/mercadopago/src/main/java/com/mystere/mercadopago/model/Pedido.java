package com.mystere.mercadopago.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer total;

    private Integer descuento;   // en pesos

    private String codigoDescuento; // 👈 NUEVO (EL CUPÓN USADO)

    private String estado;       // PAGADO / PENDIENTE / FALLIDO

    private String metodoPago;   // MERCADO_PAGO / EFECTIVO

    private LocalDateTime fecha;

    @Lob
    private String detalle;


    // ====== GETTERS & SETTERS ======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public void setDescuento(Integer descuento) {
        this.descuento = descuento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
    public String getCodigoDescuento() {
    return codigoDescuento;
}

public void setCodigoDescuento(String codigoDescuento) {
    this.codigoDescuento = codigoDescuento;
}

}
