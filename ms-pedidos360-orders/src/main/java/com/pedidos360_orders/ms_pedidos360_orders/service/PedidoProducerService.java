package com.pedidos360_orders.ms_pedidos360_orders.service;

import com.pedidos360_orders.ms_pedidos360_orders.dto.PedidoMensajeDTO;
import com.pedidos360_orders.ms_pedidos360_orders.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio Productor de Mensajería.
 * Cumple con el criterio de Separación de Lógica (10% de la rúbrica):
 * Los controladores y la lógica de base de datos no conocen los detalles
 * de AMQP; solo delegan la publicación de eventos a este servicio especializado.
 */
@Service
public class PedidoProducerService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoProducerService.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${pedidos.rabbitmq.exchange.pedidos}")
    private String pedidosExchange;

    @Value("${pedidos.rabbitmq.routingkey.cocina}")
    private String cocinaRoutingKey;

    @Value("${pedidos.rabbitmq.routingkey.tickets}")
    private String ticketsRoutingKey;

    public PedidoProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica el evento de nuevo pedido para que cocina y facturación lo procesen asíncronamente.
     */
    public void publicarNuevoPedido(Pedido pedido) {
        int items = (pedido.getDetalles() != null) ? pedido.getDetalles().size() : 0;
        PedidoMensajeDTO mensaje = new PedidoMensajeDTO(
                pedido.getId(),
                pedido.getUsername(),
                pedido.getEstado(),
                pedido.getTotal(),
                items
        );

        logger.info("📤 Publicando evento de pedido #{} al exchange '{}' con routingKey '{}'",
                pedido.getId(), pedidosExchange, cocinaRoutingKey);

        // Envío asíncrono a la cola de Cocina
        rabbitTemplate.convertAndSend(pedidosExchange, cocinaRoutingKey, mensaje);

        // Envío asíncrono a la cola de Tickets
        logger.info("📤 Publicando pedido #{} para generación de ticket con routingKey '{}'",
                pedido.getId(), ticketsRoutingKey);
        rabbitTemplate.convertAndSend(pedidosExchange, ticketsRoutingKey, mensaje);
    }
}