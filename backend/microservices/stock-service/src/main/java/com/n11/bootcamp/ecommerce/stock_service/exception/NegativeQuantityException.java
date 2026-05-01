package com.n11.bootcamp.ecommerce.stock_service.exception;

public class NegativeQuantityException extends RuntimeException{
    public NegativeQuantityException() {
        super("Invalid quantity. Quantity must be positive.");
    }
}
