package com.pedidos360_orders.ms_pedidos360_orders.service;

import com.pedidos360_orders.ms_pedidos360_orders.config.rabbitmq.RabbitMQConfig;
import com.pedidos360_orders.ms_pedidos360_orders.dto.PedidoMensajeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoProducerService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoProducerService.class);
    
    private final RabbitTemplate rabbitTemplate;

    // Inyectamos el RabbitTemplate mediante el constructor
    public PedidoProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void enviarPedidoACocina(PedidoMensajeDTO pedidoMensaje) {
        logger.info("Enviando pedido ID: {} a la cola de cocina...", pedidoMensaje.getId()); // Asumiendo que tu DTO tiene getIdPedido()
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_COCINA,
                pedidoMensaje
        );
        
        logger.info("✅ Pedido enviado a cocina exitosamente.");
    }

    /**
     * Envía el DTO del pedido a la cola de tickets a través del Exchange.
     */
    public void enviarPedidoATicket(PedidoMensajeDTO pedidoMensaje) {
        logger.info("Enviando pedido ID: {} a la cola de tickets...", pedidoMensaje.getId());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_TICKET,
                pedidoMensaje
        );
        
        logger.info("✅ Pedido enviado a tickets exitosamente.");
    }
    public void publicarNuevoPedido(com.pedidos360_orders.ms_pedidos360_orders.model.Pedido pedido) {
        logger.info("Transformando pedido ID: {} para envío a RabbitMQ...", pedido.getId());

        // 1. Mapeamos la entidad Pedido al PedidoMensajeDTO
        PedidoMensajeDTO dto = new PedidoMensajeDTO();
        dto.setId(pedido.getId());
        dto.setUsername(pedido.getUsername());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        
        // Calculamos la cantidad de items de forma segura
        int cantidad = (pedido.getDetalles() != null) ? pedido.getDetalles().size() : 0;
        dto.setCantidadItems(cantidad);

        // 2. Disparamos los mensajes usando los métodos existentes
        enviarPedidoACocina(dto);
        enviarPedidoATicket(dto);
    }
}