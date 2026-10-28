package com.pedidos360_orders.ms_pedidos360_orders.consumer.cocina;

import com.pedidos360_orders.ms_pedidos360_orders.dto.PedidoMensajeDTO;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor del Dominio de Cocina.
 */
@Component
public class CocinaConsumer {

        private static final Logger logger = LoggerFactory.getLogger(CocinaConsumer.class);

        @RabbitListener(queues = "${pedidos.rabbitmq.queue.cocina}", containerFactory = "rabbitListenerContainerFactory")
        public void procesarPedidoCocina(
                        PedidoMensajeDTO pedido,
                        Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

                logger.info("📥 [COCINA] Mensaje recibido de la cola. Procesando pedido #{} para el cliente '{}'",
                                pedido.getId(), pedido.getUsername());

                try {
                        // Regla de validación o simulación de error para demostrar la DLQ
                        if (pedido.getTotal() == null || pedido.getTotal() <= 0
                                        || "ERROR_SIMULADO".equalsIgnoreCase(pedido.getUsername())) {
                                throw new IllegalArgumentException("El pedido #" + pedido.getId()
                                                + " contiene datos inválidos o monto <= 0");
                        }

                        // --- RUTA DE ÉXITO ---
                        logger.info("🍳 [COCINA] Preparando platillos para el pedido #{} (Items: {}). Total: "
                                        + pedido.getTotal(),
                                        pedido.getId(), pedido.getCantidadItems());

                        // CONFIRMACIÓN EXPLÍCITA (ACK MANUAL)
                        channel.basicAck(deliveryTag, false);
                        logger.info("✅ [COCINA] Confirmación manual (ACK) enviada con éxito para el deliveryTag: {}",
                                        deliveryTag);

                } catch (Exception ex) {
                        // --- RUTA DE ERROR / MANEJO DE FALLAS Y DLQ ---
                        logger.error("❌ [COCINA - ERROR] Falló el procesamiento del pedido #{}: {}. Registrando en logs...",
                                        pedido.getId(), ex.getMessage());

                        // RECHAZO EXPLÍCITO (NACK) con requeue = false
                        // Al configurar requeue = false, RabbitMQ desvía el mensaje directamente a la
                        // Dead Letter Queue (DLQ)
                        channel.basicNack(deliveryTag, false, false);
                        logger.warn("⚠️ [COCINA] Mensaje del pedido #{} rechazado (NACK) y transferido a la Dead Letter Queue (pedidos.dlq)",
                                        pedido.getId());
                }
        }
}