package com.pedidos360_orders.ms_pedidos360_orders.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombres del Exchange y las Routing Keys
    public static final String EXCHANGE_NAME = "pedidos.exchange";
    public static final String ROUTING_KEY_COCINA = "pedido.cocina";
    public static final String ROUTING_KEY_TICKET = "pedido.ticket";

    // 1. Declaramos el Exchange principal
    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 2. Referenciamos las colas (Ya deben haber sido creadas por el ms-administrador)
    @Bean
    public Queue colaCocina() {
        return new Queue("cola.cocina", true);
    }

    @Bean
    public Queue colaTicket() {
        return new Queue("cola.ticket", true);
    }

    // 3. Unimos el Exchange con las colas usando las Routing Keys
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

    // 5. Inyectamos el convertidor en el RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}