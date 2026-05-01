package com.n11.bootcamp.ecommerce.shopping_cart_service.service;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {

    ResponseEntity<ShoppingCart> createShoppingCart(String name);
    ResponseEntity<ShoppingCart> addProductsToShoppingCart(Long shoppingCartId, List<Product> products);
    ResponseEntity<ShoppingCart> removeProductFromShoppingCart(Long shoppingCartId, Long productId);
    ResponseEntity<Map<String, String>> getShoppingCartPrice(Long shoppingCartId);
    ResponseEntity<ShoppingCart> getShoppingCartByShoppingCartName(String shoppingCartName);
    ResponseEntity<String> deleteShoppingCartById(Long shoppingCartId);
    ResponseEntity<List<ShoppingCart>> getAllShoppingCarts();
    ResponseEntity<String> deleteAllShoppingCarts();
    ResponseEntity<ShoppingCart> getShoppingCartById(Long shoppingCartId);
}
