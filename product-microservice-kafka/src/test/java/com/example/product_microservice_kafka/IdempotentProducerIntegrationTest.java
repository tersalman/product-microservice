package com.example.product_microservice_kafka;

import com.example.core.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@SpringBootTest
public class IdempotentProducerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    @MockBean
    KafkaAdmin kafkaAdmin;

    @Test
    void testProducerConfig_whenIdempotenceEnable_assertIdempotenceProperties() {

        //Arrange
        ProducerFactory<String, ProductCreatedEvent> producerFactory = kafkaTemplate.getProducerFactory();


        //Act
        Map<String, Object> configs = producerFactory.getConfigurationProperties();


        //Assert
        Assertions.assertEquals("true", configs.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        Assertions.assertTrue("all".equalsIgnoreCase((String) configs.get(ProducerConfig.ACKS_CONFIG)));

        if (configs.get(ProducerConfig.RETRIES_CONFIG) != null) {
            Assertions.assertTrue(
                    Integer.parseInt((String) configs.get(ProducerConfig.RETRIES_CONFIG)) > 0
            );
        }


    }

}
