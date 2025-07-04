package com.eximia.exams.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public ConnectionFactory mockConnectionFactory() {
        return Mockito.mock(ConnectionFactory.class);
    }

    @Bean
    @Primary
    public RabbitTemplate mockRabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    @Primary
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017/test");
    }

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, "test_exam_db");
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
