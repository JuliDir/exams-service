package com.eximia.exams.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXAM_EXCHANGE = "exam.exchange";

    public static final String EXAM_REQUEST_ROUTING_KEY = "exam.request";
    public static final String EXAM_CREATED_ROUTING_KEY = "exam.created";
    public static final String EXAM_FAILED_ROUTING_KEY = "exam.failed";

    public static final String EXAM_REQUEST_QUEUE = "exam.request.queue";
    public static final String EXAM_CREATED_QUEUE = "exam.created.queue";
    public static final String EXAM_FAILED_QUEUE = "exam.failed.queue";

    @Bean
    public DirectExchange examExchange() {
        return new DirectExchange(EXAM_EXCHANGE);
    }

    @Bean
    public DirectExchange examDLX() {
        return new DirectExchange("exam.dlx");
    }

    @Bean
    public Queue examRequestQueue() {
        return QueueBuilder
                .durable(EXAM_REQUEST_QUEUE)
                .withArgument("x-dead-letter-exchange", "exam.dlx")
                .withArgument("x-dead-letter-routing-key", EXAM_FAILED_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue examCreatedQueue() {
        return QueueBuilder
                .durable(EXAM_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Queue examFailedQueue() {
        return QueueBuilder
                .durable(EXAM_FAILED_QUEUE)
                .build();
    }

    @Bean
    public Binding examRequestBinding() {
        return BindingBuilder
                .bind(examRequestQueue())
                .to(examExchange())
                .with(EXAM_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding examCreatedBinding() {
        return BindingBuilder
                .bind(examCreatedQueue())
                .to(examExchange())
                .with(EXAM_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding examFailedBinding() {
        return BindingBuilder
                .bind(examFailedQueue())
                .to(examDLX())
                .with(EXAM_FAILED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
