package com.pedidos360_orders.ms_pedidos360_orders.consumer.ticket;

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
 * Consumidor del Dominio de Facturación / Tickets.
 */
@Component
public class TicketConsumer {

        private static final Logger logger = LoggerFactory.getLogger(TicketConsumer.class);

        @RabbitListener(queues = "${pedidos.rabbitmq.queue.tickets}", containerFactory = "rabbitListenerContainerFactory")
        public void generarTicketPedido(
                        PedidoMensajeDTO pedido,
                        Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

                logger.info("📥 [TICKETS] Generando boleta electrónica para el pedido #{} del cliente '{}'",
                                pedido.getId(), pedido.getUsername());

                try {
                        // Lógica de emisión de boleta digital
                        logger.info("🧾 [TICKETS] Boleta N° TCK-{} generada exitosamente. Monto: " + pedido.getTotal(),
                                        pedido.getId());

                        // ACK explícito
                        channel.basicAck(deliveryTag, false);
                        logger.info("✅ [TICKETS] ACK manual confirmado para ticket del pedido #{}", pedido.getId());

                } catch (Exception ex) {
                        logger.error("❌ [TICKETS - ERROR] Error al generar ticket para pedido #{}: {}",
                                        pedido.getId(), ex.getMessage());
                        channel.basicNack(deliveryTag, false, false);
                }
        }
}