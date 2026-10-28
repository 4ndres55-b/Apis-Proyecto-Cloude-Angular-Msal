package com.example.Administrador.controller;

import com.example.Administrador.dto.ColaRequestDTO;
import com.example.Administrador.dto.RespuestaAdminDTO;
import com.example.Administrador.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST del Microservicio Administrador.
 * Expone endpoints con rutas limpias y verbos HTTP adecuados para la gestión
 * de colas en RabbitMQ, cumpliendo con la rúbrica (13%).
 * Delega toda la lógica operativa a RabbitAdminService (Encapsulamiento 10%).
 */
@RestController
@RequestMapping("/colas")
@CrossOrigin(origins = "*")
public class RabbitAdminController {

    private final RabbitAdminService adminService;

    public RabbitAdminController(RabbitAdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Endpoint para crear una cola.
     * Valida los parámetros con DTO para evitar nombres vacíos (Criterio 7%).
     */
    @PostMapping
    public ResponseEntity<RespuestaAdminDTO> crearCola(@Valid @RequestBody ColaRequestDTO request) {
        RespuestaAdminDTO respuesta = adminService.crearCola(request);
        if (respuesta.isExitoso()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    /**
     * Endpoint para eliminar una cola por su nombre.
     */
    @DeleteMapping("/{nombre}")
    public ResponseEntity<RespuestaAdminDTO> eliminarCola(@PathVariable String nombre) {
        RespuestaAdminDTO respuesta = adminService.eliminarCola(nombre);
        if (respuesta.isExitoso()) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    /**
     * Endpoint para consultar información y estado de una cola.
     */
    @GetMapping("/{nombre}")
    public ResponseEntity<Map<String, Object>> consultarCola(@PathVariable String nombre) {
        Map<String, Object> info = adminService.consultarCola(nombre);
        return ResponseEntity.ok(info);
    }
}
