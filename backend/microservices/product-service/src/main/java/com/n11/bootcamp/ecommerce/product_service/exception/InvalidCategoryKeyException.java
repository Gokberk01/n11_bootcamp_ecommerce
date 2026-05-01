package com.n11.bootcamp.ecommerce.product_service.exception;

public class InvalidCategoryKeyException extends RuntimeException {
    public InvalidCategoryKeyException(String oldKey, String newKey) {
        super("Invalid category key update: " + oldKey + " -> " + newKey);
    }
}

