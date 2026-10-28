package com.example.Administrador.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta estándar para las operaciones del microservicio Administrador.
 */
public class RespuestaAdminDTO {

    private boolean exitoso;
    private String mensaje;
    private String nombreCola;
    private LocalDateTime timestamp;

    public RespuestaAdminDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public RespuestaAdminDTO(boolean exitoso, String mensaje, String nombreCola) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.nombreCola = nombreCola;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombreCola() {
        return nombreCola;
    }

    public void setNombreCola(String nombreCola) {
        this.nombreCola = nombreCola;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
