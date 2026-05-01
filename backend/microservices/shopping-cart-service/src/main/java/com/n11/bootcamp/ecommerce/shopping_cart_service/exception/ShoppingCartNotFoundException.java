package com.n11.bootcamp.ecommerce.shopping_cart_service.exception;

public class ShoppingCartNotFoundException extends RuntimeException {
    public ShoppingCartNotFoundException(Long id) {
        super("Shopping cart not found with id: " + id);
    }

    public ShoppingCartNotFoundException(String name) {
        super("Shopping cart not found with name: " + name);
    }
}