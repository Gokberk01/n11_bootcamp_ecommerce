package com.n11.bootcamp.ecommerce.stock_service.entity;

import com.n11.bootcamp.ecommerce.stock_service.exception.InsufficientStockException;
import com.n11.bootcamp.ecommerce.stock_service.exception.NegativeQuantityException;
import jakarta.persistence.*;

@Entity
@Table(name = "product_stock")
public class Stock {

    @Id
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    public Stock() {}

    public Stock(Long productId, String productName, Integer availableQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }


    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }
    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }


    public void reserve(int quantity) {
        validatePositiveOrZero(quantity);

        if (availableQuantity < quantity) {
            throw new InsufficientStockException("Insufficient stock");
        }

        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    public void release(int quantity) {
        validatePositiveOrZero(quantity);

        if (reservedQuantity < quantity) {
            throw new InsufficientStockException("Insufficient reserved stock");
        }

        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }

    public void commit(int quantity) {
        validatePositiveOrZero(quantity);

        if (reservedQuantity < quantity) {
            throw new InsufficientStockException("Insufficient reserved stock");
        }

        reservedQuantity -= quantity;
    }

    private void validatePositiveOrZero(int quantity) {
        if (quantity < 0) {
            throw new NegativeQuantityException();
        }
    }
}