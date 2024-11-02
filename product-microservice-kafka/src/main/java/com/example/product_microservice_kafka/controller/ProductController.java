package com.example.product_microservice_kafka.controller;

import com.example.product_microservice_kafka.service.ProductService;
import com.example.product_microservice_kafka.service.dto.CreatedProductDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/product")

public class ProductController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody CreatedProductDto createdProductDto){
        String productId = null;
        try {
            productId = productService.createProductDto(createdProductDto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(new Date(),"message"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

}
