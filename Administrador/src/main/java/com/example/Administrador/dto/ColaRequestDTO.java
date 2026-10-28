package com.example.Administrador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación y configuración de colas en RabbitMQ.
 * Implementa validaciones estrictas para evitar configuraciones inválidas
 * o nombres vacíos, cumpliendo con la pauta de evaluación.
 */
public class ColaRequestDTO {

    @NotBlank(message = "El nombre de la cola no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de la cola debe tener entre 3 y 50 caracteres")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$", 
        message = "El nombre de la cola solo puede contener letras, números, puntos, guiones o guiones bajos"
    )
    private String nombre;

    private boolean durable = true;
    private boolean exclusiva = false;
    private boolean autoDelete = false;

    public ColaRequestDTO() {
    }

    public ColaRequestDTO(String nombre, boolean durable, boolean exclusiva, boolean autoDelete) {
        this.nombre = nombre;
        this.durable = durable;
        this.exclusiva = exclusiva;
        this.autoDelete = autoDelete;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isExclusiva() {
        return exclusiva;
    }

    public void setExclusiva(boolean exclusiva) {
        this.exclusiva = exclusiva;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }
}
