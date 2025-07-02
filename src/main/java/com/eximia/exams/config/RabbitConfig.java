package com.eximia.exams.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXAM_QUEUE = "exam.queue";
    public static final String EXAM_EXCHANGE = "exam.exchange";
    public static final String EXAM_ROUTING_KEY = "exam.created";

    @Bean
    public Queue examQueue() {
        return QueueBuilder
                .durable(EXAM_QUEUE)
                .withArgument("x-dead-letter-exchange", "exam.dlx")
                .withArgument("x-dead-letter-routing-key", "exam.failed")
                .build();
    }

    @Bean
    public TopicExchange examExchange() {
        return new TopicExchange(EXAM_EXCHANGE);
    }

    @Bean
    public Binding examBinding() {
        return BindingBuilder
                .bind(examQueue())
                .to(examExchange())
                .with(EXAM_ROUTING_KEY);
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
