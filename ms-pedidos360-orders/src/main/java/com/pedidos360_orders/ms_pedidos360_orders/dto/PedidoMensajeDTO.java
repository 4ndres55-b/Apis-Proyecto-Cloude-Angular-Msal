package com.pedidos360_orders.ms_pedidos360_orders.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para la transferencia de eventos de pedidos a través de RabbitMQ.
 */
public class PedidoMensajeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String estado;
    private Double total;
    private int cantidadItems;
    private String fecha;

    public PedidoMensajeDTO() {
        this.fecha = LocalDateTime.now().toString();
    }

    public PedidoMensajeDTO(Long id, String username, String estado, Double total, int cantidadItems) {
        this.id = id;
        this.username = username;
        this.estado = estado;
        this.total = total;
        this.cantidadItems = cantidadItems;
        this.fecha = LocalDateTime.now().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public int getCantidadItems() {
        return cantidadItems;
    }

    public void setCantidadItems(int cantidadItems) {
        this.cantidadItems = cantidadItems;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}