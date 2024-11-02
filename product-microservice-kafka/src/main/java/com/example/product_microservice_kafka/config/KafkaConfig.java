package com.example.product_microservice_kafka.config;


import com.example.core.ProductCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;
    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;
    @Value("${spring.kafka.producer.acks}")
    private String acks;
    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private String deliveryTimeout;
    @Value("${spring.kafka.producer.properties.linger.ms}")
    private String linger;
    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private String timeRequest;
    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private String impotence;
    @Value("${spring.kafka.producer.properties.max.in.flight.request.per.connection}")
    private String maxInFlightRequest;


    Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();

        config.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        config.put(ACKS_CONFIG, acks);
        config.put(DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        config.put(LINGER_MS_CONFIG, linger);
        config.put(REQUEST_TIMEOUT_MS_CONFIG, timeRequest);
        config.put(ENABLE_IDEMPOTENCE_CONFIG, impotence);
        config.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequest);
        config.put(RETRIES_CONFIG, Integer.MAX_VALUE);

        return config;
    }

    @Bean
    ProducerFactory<String, ProductCreatedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }




    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name("product-created-events-topic")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

}
