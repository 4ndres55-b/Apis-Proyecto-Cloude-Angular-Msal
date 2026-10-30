package com.pedidos360_orders.ms_pedidos360_orders.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "pedidos.exchange";
    public static final String ROUTING_KEY_COCINA = "pedido.cocina";
    public static final String ROUTING_KEY_TICKET = "pedido.ticket";

    public static final String DLX_NAME = "pedidos.dlx";
    public static final String DLQ_NAME = "cola.dlq";
    public static final String ROUTING_KEY_DLQ = "pedido.dlq";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME);
    }

    @Bean
    public Queue colaDlq() {
        return new Queue(DLQ_NAME, true);
    }

    @Bean
    public Binding bindingDlq(Queue colaDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(colaDlq).to(deadLetterExchange).with(ROUTING_KEY_DLQ);
    }

    @Bean
    public Queue colaCocina() {
        Map<String, Object> args = new HashMap<>();
        // Le decimos a RabbitMQ: "Si un mensaje falla aquí, envíalo al DLX"
        args.put("x-dead-letter-exchange", DLX_NAME);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_DLQ);
        return new Queue("cola.cocina", true, false, false, args);
    }

    @Bean
    public Queue colaTicket() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_NAME);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_DLQ);
        return new Queue("cola.ticket", true, false, false, args);
    }

    @Bean
    public Binding bindingCocina(Queue colaCocina, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(colaCocina).to(pedidosExchange).with(ROUTING_KEY_COCINA);
    }

    @Bean
    public Binding bindingTicket(Queue colaTicket, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(colaTicket).to(pedidosExchange).with(ROUTING_KEY_TICKET);
    }
    
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