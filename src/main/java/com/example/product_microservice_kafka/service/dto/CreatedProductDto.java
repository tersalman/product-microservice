package com.example.product_microservice_kafka.service.dto;

import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

public class CreatedProductDto {

    private String title;
    private BigDecimal price;
    private Integer quantity;

    public CreatedProductDto(String title, BigDecimal price, Integer quantity) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CreatedProductDto{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreatedProductDto that = (CreatedProductDto) o;
        return Objects.equals(title, that.title) && Objects.equals(price, that.price) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, price, quantity);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
