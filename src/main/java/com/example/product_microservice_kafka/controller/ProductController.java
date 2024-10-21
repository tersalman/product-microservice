package com.example.product_microservice_kafka.controller;

import com.example.product_microservice_kafka.service.dto.CreatedProductDto;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody CreatedProductDto createdProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

}
