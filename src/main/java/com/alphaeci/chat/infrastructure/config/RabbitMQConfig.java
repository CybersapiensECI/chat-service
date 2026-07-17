package com.alphaeci.chat.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queues.friendship}")
    private String friendshipQueue;

    @Value("${rabbitmq.queues.parche-created}")
    private String parcheCreatedQueue;

    @Value("${rabbitmq.queues.parche-member-joined}")
    private String parcheMemberJoinedQueue;

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange("notification.exchange");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    // ── Amistad nueva -> chat directo ACTIVE (profile-service) ─────────────

    @Bean
    public Queue friendshipQueue() {
        return QueueBuilder.durable(friendshipQueue).build();
    }

    @Bean
    public Binding friendshipBinding() {
        return BindingBuilder.bind(friendshipQueue()).to(notificationExchange()).with("friendship.created");
    }

    // ── Parche nuevo / miembro nuevo -> sala grupal (Parches-Service) ──────

    @Bean
    public Queue parcheCreatedQueue() {
        return QueueBuilder.durable(parcheCreatedQueue).build();
    }

    @Bean
    public Binding parcheCreatedBinding() {
        return BindingBuilder.bind(parcheCreatedQueue()).to(notificationExchange()).with("parche.created");
    }

    @Bean
    public Queue parcheMemberJoinedQueue() {
        return QueueBuilder.durable(parcheMemberJoinedQueue).build();
    }

    @Bean
    public Binding parcheMemberJoinedBinding() {
        return BindingBuilder.bind(parcheMemberJoinedQueue()).to(notificationExchange()).with("parche.member-joined");
    }
}
