package com.example.product_microservice_kafka.service;


import com.example.product_microservice_kafka.service.dto.CreatedProductDto;

import java.util.concurrent.ExecutionException;

public interface ProductService {

    String createProductDto(CreatedProductDto createdProductDto) throws ExecutionException, InterruptedException;
}
