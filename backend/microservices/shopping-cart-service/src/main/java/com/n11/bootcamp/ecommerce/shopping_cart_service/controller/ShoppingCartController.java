package com.n11.bootcamp.ecommerce.shopping_cart_service.controller;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;

import com.n11.bootcamp.ecommerce.shopping_cart_service.service.impl.ShoppingCartServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartServiceImpl shoppingCartServiceImpl;

    public ShoppingCartController(ShoppingCartServiceImpl shoppingCartServiceImpl)
    {
        this.shoppingCartServiceImpl = shoppingCartServiceImpl;
    }

    @PostMapping
    public ResponseEntity<ShoppingCart> createShoppingCart(@RequestParam("name") String name) {
        return shoppingCartServiceImpl.createShoppingCart(name);
    }

    @PostMapping("{shoppingCartId}")
    public ResponseEntity<ShoppingCart> addProductsToShoppingCart(
            @PathVariable("shoppingCartId") Long shoppingCartId,
            @RequestBody List<Product> products) {
        return shoppingCartServiceImpl.addProductsToShoppingCart(shoppingCartId, products);
    }

    @DeleteMapping("/{shoppingCartId}/products/{productId}")
    public ResponseEntity<ShoppingCart> removeProductFromShoppingCart(
            @PathVariable("shoppingCartId") Long shoppingCartId,
            @PathVariable("productId") Long productId) {
        return shoppingCartServiceImpl.removeProductFromShoppingCart(shoppingCartId, productId);
    }

    @GetMapping("/totalprice/{id}")
    public ResponseEntity<Map<String, String>> getTotalPrice(
            @PathVariable("id") Long shoppingCartId) {
        return shoppingCartServiceImpl.getShoppingCartPrice(shoppingCartId);
    }

    @GetMapping("{id}")
    public ResponseEntity<ShoppingCart> getShoppingCartById(@PathVariable("id") Long shoppingCartId) {
        return shoppingCartServiceImpl.getShoppingCartById(shoppingCartId);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<ShoppingCart> getShoppingCartByShoppingCartName(@PathVariable("name") String shoppingCartName) {
        return shoppingCartServiceImpl.getShoppingCartByShoppingCartName(shoppingCartName);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteShoppingCartById(@PathVariable("id") Long shoppingCartId) {
        return shoppingCartServiceImpl.deleteShoppingCartById(shoppingCartId);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllShoppingCarts() {
        return shoppingCartServiceImpl.deleteAllShoppingCarts();
    }
}
