package com.example.email_notification_microservice.handler;

import com.example.core.ProductCreatedEvent;
import com.example.email_notification_microservice.exception.NonRetryableException;
import com.example.email_notification_microservice.exception.RetryableException;
import com.example.email_notification_microservice.persistance.entity.ProcessedEventEntity;
import com.example.email_notification_microservice.persistance.repository.ProcessedEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    ProcessedEventRepository processedEventRepository;
    RestTemplate restTemplate;


    public ProductCreatedEventHandler(ProcessedEventRepository processedEventRepository, RestTemplate restTemplate) {
        this.processedEventRepository = processedEventRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        LOGGER.info("Received event: {}", productCreatedEvent.getTitle());

        ProcessedEventEntity processedEventEntity = processedEventRepository.findByMessageId(messageId);

        if (processedEventEntity != null) {
            LOGGER.info("Duplicate messageId : {}", messageId);
        }

        try {
            String url = "http://localhost:8090/response/200";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response: {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }

        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId, productCreatedEvent.getProductId()));
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("error {}", e.getMessage());
            throw new NonRetryableException(e);
        }

    }


}
