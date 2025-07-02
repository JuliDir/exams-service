package com.eximia.exams.health;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component("examServiceHealth")
@RequiredArgsConstructor
public class ExamServiceHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Health health() {
        try {
            // Check MongoDB connection
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));

            // Check RabbitMQ connection
            rabbitTemplate.getConnectionFactory().createConnection().close();

            return Health.up()
                    .withDetail("mongodb", "Available")
                    .withDetail("rabbitmq", "Available")
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
