package com.pedidos360_orders.ms_pedidos360_orders.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de Configuración Centralizada de RabbitMQ.
 */
@Configuration
public class RabbitMQConfig {

    // --- Lectura Centralizada de Exchanges ---
    @Value("${pedidos.rabbitmq.exchange.pedidos}")
    private String pedidosExchangeName;

    @Value("${pedidos.rabbitmq.exchange.dlq}")
    private String dlqExchangeName;

    // --- Lectura Centralizada de Colas ---
    @Value("${pedidos.rabbitmq.queue.cocina}")
    private String cocinaQueueName;

    @Value("${pedidos.rabbitmq.queue.tickets}")
    private String ticketsQueueName;

    @Value("${pedidos.rabbitmq.queue.dlq}")
    private String dlqQueueName;

    // --- Lectura Centralizada de Routing Keys ---
    @Value("${pedidos.rabbitmq.routingkey.cocina}")
    private String cocinaRoutingKey;

    @Value("${pedidos.rabbitmq.routingkey.tickets}")
    private String ticketsRoutingKey;

    @Value("${pedidos.rabbitmq.routingkey.dlq}")
    private String dlqRoutingKey;

    // =========================================================================
    // EXCHANGES
    // =========================================================================

    /**
     * Exchange principal para la distribución de eventos de pedidos.
     */
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(pedidosExchangeName, true, false);
    }

    /**
     * Exchange exclusivo para mensajes fallidos (Dead Letter Exchange).
     */
    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(dlqExchangeName, true, false);
    }

    // =========================================================================
    // COLAS (QUEUES)
    // =========================================================================

    /**
     * Cola para el despacho a cocina.
     * Configurada con x-dead-letter-exchange para que los mensajes rechazados
     * con NACK viajen automáticamente a la DLQ (Manejo de Errores - 20%).
     */
    @Bean
    public Queue cocinaQueue() {
        return QueueBuilder.durable(cocinaQueueName)
                .withArgument("x-dead-letter-exchange", dlqExchangeName)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    /**
     * Cola para la generación asíncrona de tickets/boletas.
     */
    @Bean
    public Queue ticketsQueue() {
        return QueueBuilder.durable(ticketsQueueName)
                .withArgument("x-dead-letter-exchange", dlqExchangeName)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    /**
     * Dead Letter Queue (DLQ): Almacena de forma persistente los mensajes que
     * fallaron
     * durante el procesamiento para auditoría y análisis de incidentes.
     */
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(dlqQueueName).build();
    }

    // =========================================================================
    // ENLACES (BINDINGS)
    // =========================================================================

    @Bean
    public Binding cocinaBinding() {
        return BindingBuilder.bind(cocinaQueue())
                .to(pedidosExchange())
                .with(cocinaRoutingKey);
    }

    @Bean
    public Binding ticketsBinding() {
        return BindingBuilder.bind(ticketsQueue())
                .to(pedidosExchange())
                .with(ticketsRoutingKey);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue())
                .to(dlqExchange())
                .with(dlqRoutingKey);
    }

    // =========================================================================
    // CONVERTIDOR DE MENSAJES JSON Y TEMPLATE
    // =========================================================================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}