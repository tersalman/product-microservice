package com.example.email_notification_microservice.handler;

import com.example.core.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        LOGGER.info("Received event: {}", productCreatedEvent.getTitle() );
    }


}
