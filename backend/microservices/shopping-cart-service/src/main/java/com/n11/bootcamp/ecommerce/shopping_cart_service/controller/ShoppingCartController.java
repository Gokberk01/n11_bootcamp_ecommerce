package com.n11.bootcamp.ecommerce.shopping_cart_service.controller;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;

import com.n11.bootcamp.ecommerce.shopping_cart_service.service.impl.ShoppingCartServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/shopping-cart")
@Tag(name = "Shopping Cart", description = "Shopping Cart Management APIs")
public class ShoppingCartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartController.class);

    private final ShoppingCartServiceImpl shoppingCartServiceImpl;

    public ShoppingCartController(ShoppingCartServiceImpl shoppingCartServiceImpl)
    {
        this.shoppingCartServiceImpl = shoppingCartServiceImpl;
    }

    @Operation(summary = "Create a new shopping cart", description = "Initializes a new shopping cart for a user with the given name.")
    @PostMapping
    public ResponseEntity<ShoppingCart> createShoppingCart(@RequestBody String name) {
        LOGGER.info("API CALL: Create shopping cart for name={}", name);
        return shoppingCartServiceImpl.createShoppingCart(name);
    }

    @Operation(summary = "Add products to shopping cart", description = "Adds a list of products to an existing shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products successfully added"),
            @ApiResponse(responseCode = "404", description = "Shopping cart not found")
    })
    @PostMapping("{shoppingCartId}")
    public ResponseEntity<ShoppingCart> addProductsToShoppingCart(
            @PathVariable("shoppingCartId") Long shoppingCartId,
            @RequestBody List<Product> products) {
        LOGGER.info("API CALL: Add products to shopping cart id={}", shoppingCartId);
        return shoppingCartServiceImpl.addProductsToShoppingCart(shoppingCartId, products);
    }

    @Operation(summary = "Remove a product from shopping cart")
    @DeleteMapping("/{shoppingCartId}/products/{productId}")
    public ResponseEntity<ShoppingCart> removeProductFromShoppingCart(
            @PathVariable("shoppingCartId") Long shoppingCartId,
            @PathVariable("productId") Long productId) {
        LOGGER.warn("API CALL: Remove product id={} from shopping cart id={}", productId, shoppingCartId);
        return shoppingCartServiceImpl.removeProductFromShoppingCart(shoppingCartId, productId);
    }

    @Operation(summary = "Calculate total price", description = "Returns the total price of all items in the specified shopping cart.")
    @GetMapping("/totalprice/{id}")
    public ResponseEntity<Map<String, String>> getTotalPrice(
            @PathVariable("id") Long shoppingCartId) {
        LOGGER.info("API CALL: Get total price for shopping cart id={}", shoppingCartId);
        return shoppingCartServiceImpl.getShoppingCartPrice(shoppingCartId);
    }

    @Operation(summary = "Get shopping cart by ID")
    @GetMapping("{id}")
    public ResponseEntity<ShoppingCart> getShoppingCartById(@PathVariable("id") Long shoppingCartId) {
        LOGGER.info("API CALL: Get shopping cart id={}", shoppingCartId);
        return shoppingCartServiceImpl.getShoppingCartById(shoppingCartId);
    }

    @Operation(summary = "Get shopping cart by name")
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ShoppingCart> getShoppingCartByShoppingCartName(@PathVariable("name") String shoppingCartName) {
        LOGGER.info("API CALL: Get shopping cart by name={}", shoppingCartName);
        return shoppingCartServiceImpl.getShoppingCartByShoppingCartName(shoppingCartName);
    }

    @Operation(summary = "Delete shopping cart by ID")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteShoppingCartById(@PathVariable("id") Long shoppingCartId) {
        LOGGER.warn("API CALL: Delete shopping cart id={}", shoppingCartId);
        return shoppingCartServiceImpl.deleteShoppingCartById(shoppingCartId);
    }

    @Operation(summary = "Delete ALL shopping carts", description = "Danger: This will clear all shopping carts in the system.")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllShoppingCarts() {
        LOGGER.warn("API CALL: Delete ALL shopping carts");
        return shoppingCartServiceImpl.deleteAllShoppingCarts();
    }
}
