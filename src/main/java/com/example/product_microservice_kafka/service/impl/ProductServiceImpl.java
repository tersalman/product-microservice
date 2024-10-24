package com.example.product_microservice_kafka.service.impl;

import com.example.product_microservice_kafka.service.ProductService;
import com.example.product_microservice_kafka.service.dto.CreatedProductDto;
import com.example.product_microservice_kafka.service.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String,ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProductDto(CreatedProductDto createdProductDto) throws ExecutionException, InterruptedException {
    //TODO save to DB
        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId, createdProductDto.getTitle(), createdProductDto.getPrice(), createdProductDto.getQuantity());

        SendResult<String, ProductCreatedEvent> result= kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent).get();

        LOGGER.info("Topic {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset {}", result.getRecordMetadata().offset());

        LOGGER.info("Return {}", productId);

        return productId;
    }
}
