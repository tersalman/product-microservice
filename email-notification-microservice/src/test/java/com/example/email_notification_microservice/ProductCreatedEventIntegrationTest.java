package com.example.email_notification_microservice;

import com.example.core.ProductCreatedEvent;
import com.example.email_notification_microservice.handler.ProductCreatedEventHandler;
import com.example.email_notification_microservice.persistance.entity.ProcessedEventEntity;
import com.example.email_notification_microservice.persistance.repository.ProcessedEventRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@EmbeddedKafka
@SpringBootTest(properties = "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}")

public class ProductCreatedEventIntegrationTest {
    @MockBean
    ProcessedEventRepository processedEventRepository;
    @MockBean
    RestTemplate restTemplate;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @SpyBean
    ProductCreatedEventHandler handler;

    @Test
    public void testProductCreatedEventHandler_OnProductCreated_HandlesEvents() throws ExecutionException, InterruptedException {
        // Arrange
        ProductCreatedEvent event = new ProductCreatedEvent();
        event.setTitle("Samsung");
        event.setProductId(UUID.randomUUID().toString());
        event.setPrice(new BigDecimal("1000"));
        event.setQuantity(10);

        String messageId = UUID.randomUUID().toString();
        String messageKey = event.getProductId();

        ProducerRecord<String,Object> record = new ProducerRecord<>("product-created-events-topic",
                messageKey,
                event);
        record.headers().add("messageId", messageId.getBytes());
        record.headers().add(KafkaHeaders.RECEIVED_KEY, messageKey.getBytes());

        ProcessedEventEntity product = new ProcessedEventEntity();
        when(processedEventRepository.findByMessageId(anyString())).thenReturn(product);
        when(processedEventRepository.save(any(ProcessedEventEntity.class))).thenReturn(null);

        String responseBody = "{\"key\":\"value\"}";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                isNull(),
                eq(String.class)
        ))
                .thenReturn(responseEntity);

        // Act
        kafkaTemplate.send(record).get();
        // Assert

        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);

        verify(handler, timeout(5000).times(1)).handle(eventCaptor.capture(),
                messageIdCaptor.capture(),
                messageKeyCaptor.capture());


        assertEquals(messageId, messageIdCaptor.getValue());
        assertEquals(messageKey, messageKeyCaptor.getValue());
        assertEquals(event.getProductId(), eventCaptor.getValue().getProductId());


    }

}
