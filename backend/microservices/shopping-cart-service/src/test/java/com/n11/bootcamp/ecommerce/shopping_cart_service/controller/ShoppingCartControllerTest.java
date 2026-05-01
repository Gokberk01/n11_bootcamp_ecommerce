package com.n11.bootcamp.ecommerce.shopping_cart_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;
import com.n11.bootcamp.ecommerce.shopping_cart_service.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingCartController.class)
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShoppingCartServiceImpl shoppingCartService;

    @Autowired
    private ObjectMapper objectMapper;

    private ShoppingCart sampleCart;

    @BeforeEach
    void setUp() {
        sampleCart = new ShoppingCart();
        sampleCart.setId(1L);
        sampleCart.setShoppingCartName("TestUser");
    }

    @Test
    void createShoppingCart_ShouldReturnOk() throws Exception {
        when(shoppingCartService.createShoppingCart(anyString())).thenReturn(ResponseEntity.ok(sampleCart));

        mockMvc.perform(post("/api/shopping-cart")
                        .contentType("application/json")
                        .content("\"TestUser\"")) // JSON strings must be wrapped in quotes
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shoppingCartName").value("TestUser"));
    }

    @Test
    void addProductsToShoppingCart_ShouldReturnOk() throws Exception {
        List<Product> products = new ArrayList<>();
        when(shoppingCartService.addProductsToShoppingCart(anyLong(), anyList())).thenReturn(ResponseEntity.ok(sampleCart));

        mockMvc.perform(post("/api/shopping-cart/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(products)))
                .andExpect(status().isOk());
    }

    @Test
    void removeProductFromShoppingCart_ShouldReturnOk() throws Exception {
        when(shoppingCartService.removeProductFromShoppingCart(1L, 101L)).thenReturn(ResponseEntity.ok(sampleCart));

        mockMvc.perform(delete("/api/shopping-cart/1/products/101"))
                .andExpect(status().isOk());
    }

    @Test
    void getTotalPrice_ShouldReturnMap() throws Exception {
        Map<String, String> priceMap = new HashMap<>();
        priceMap.put("total_price", "500.0");
        when(shoppingCartService.getShoppingCartPrice(1L)).thenReturn(ResponseEntity.ok(priceMap));

        mockMvc.perform(get("/api/shopping-cart/totalprice/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_price").value("500.0"));
    }

    @Test
    void getShoppingCartById_ShouldReturnCart() throws Exception {
        when(shoppingCartService.getShoppingCartById(1L)).thenReturn(ResponseEntity.ok(sampleCart));

        mockMvc.perform(get("/api/shopping-cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getShoppingCartByShoppingCartName_ShouldReturnCart() throws Exception {
        when(shoppingCartService.getShoppingCartByShoppingCartName("TestUser")).thenReturn(ResponseEntity.ok(sampleCart));

        mockMvc.perform(get("/api/shopping-cart/by-name/TestUser"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteShoppingCartById_ShouldReturnMessage() throws Exception {
        when(shoppingCartService.deleteShoppingCartById(1L)).thenReturn(ResponseEntity.ok("Deleted"));

        mockMvc.perform(delete("/api/shopping-cart/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));
    }

    @Test
    void deleteAllShoppingCarts_ShouldReturnMessage() throws Exception {
        when(shoppingCartService.deleteAllShoppingCarts()).thenReturn(ResponseEntity.ok("All Deleted"));

        mockMvc.perform(delete("/api/shopping-cart/deleteAll"))
                .andExpect(status().isOk())
                .andExpect(content().string("All Deleted"));
    }
}

