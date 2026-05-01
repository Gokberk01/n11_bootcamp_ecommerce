package com.n11.bootcamp.ecommerce.user_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String identifier) {
        super("User not found with: " + identifier);
    }
}
