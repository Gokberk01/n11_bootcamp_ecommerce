package com.n11.bootcamp.ecommerce.stock_service.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(Long productId) {
        super("Stock not found for product id: " + productId);
    }
}
