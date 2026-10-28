package com.pedidos360_orders.ms_pedidos360_orders.controller;

import com.pedidos360_orders.ms_pedidos360_orders.model.Pedido;
import com.pedidos360_orders.ms_pedidos360_orders.repository.PedidoRepository;
import com.pedidos360_orders.ms_pedidos360_orders.service.PedidoProducerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de Pedidos.
 * Integra la persistencia en AWS RDS con la publicación asíncrona de eventos a RabbitMQ
 * mediante el servicio desacoplado PedidoProducerService (Separación de Lógica - 10%).
 */
@RestController
@RequestMapping("/api/orders")
public class PedidoController {

    private final PedidoRepository repository;
    private final PedidoProducerService producerService;

    public PedidoController(PedidoRepository repository, PedidoProducerService producerService) {
        this.repository = repository;
        this.producerService = producerService;
    }

    // Leer todos los pedidos (Para Admin y Operador)
    @GetMapping
    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    // Crear un nuevo pedido (Para Cliente)
    @PostMapping
    public Pedido crear(@RequestBody Pedido pedido) {
        pedido.setEstado("CREADO");
        Pedido guardado = repository.save(pedido);

        // Disparo del evento asíncrono hacia RabbitMQ (Cocina y Facturación)
        producerService.publicarNuevoPedido(guardado);

        return guardado;
    }

    // Cambiar el estado del pedido (Para Operador)
    @PutMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        return repository.save(pedido);
    }
}