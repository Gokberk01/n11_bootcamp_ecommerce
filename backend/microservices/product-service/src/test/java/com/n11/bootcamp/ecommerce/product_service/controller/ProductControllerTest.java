package com.n11.bootcamp.ecommerce.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.bootcamp.ecommerce.product_service.entity.Product;
import com.n11.bootcamp.ecommerce.product_service.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductServiceImpl productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product createProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setTitle("Test Product");
        return p;
    }

    // ---------- CREATE ----------
    @Test
    void shouldCreateProduct() throws Exception {
        Product product = createProduct();

        Mockito.when(productService.createProduct(any()))
                .thenReturn(ResponseEntity.ok(product));

        mockMvc.perform(post("/api/product")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Product"));
    }

    // ---------- UPDATE ----------
    @Test
    void shouldUpdateProduct() throws Exception {
        Product product = createProduct();

        Mockito.when(productService.updateProduct(eq(1L), any()))
                .thenReturn(ResponseEntity.ok(product));

        mockMvc.perform(put("/api/product/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    // ---------- DELETE BY ID ----------
    @Test
    void shouldDeleteById() throws Exception {
        Mockito.when(productService.deleteProduct(1L))
                .thenReturn(ResponseEntity.ok("deleted"));

        mockMvc.perform(delete("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));
    }

    // ---------- DELETE ALL ----------
    @Test
    void shouldDeleteAll() throws Exception {
        Mockito.when(productService.deleteAllProducts())
                .thenReturn(ResponseEntity.ok("all deleted"));

        mockMvc.perform(delete("/api/product/deleteAll"))
                .andExpect(status().isOk())
                .andExpect(content().string("all deleted"));
    }

    // ---------- GET ALL ----------
    @Test
    void shouldGetAllProducts() throws Exception {
        Product product = createProduct();

        Mockito.when(productService.allProducts())
                .thenReturn(ResponseEntity.ok(List.of(product)));

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Product"));
    }

    // ---------- GET BY ID ----------
    @Test
    void shouldGetProductById() throws Exception {
        Product product = createProduct();

        Mockito.when(productService.getProductById(1L))
                .thenReturn(ResponseEntity.ok(product));

        mockMvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Product"));
    }

    // ---------- PAGED ----------
    @Test
    void shouldGetPagedProducts() throws Exception {
        Product product = createProduct();
        Page<Product> page = new PageImpl<>(
                List.of(product),
                PageRequest.of(0, 4),
                1
        );

        Mockito.when(productService.getPaged(0, 4)).thenReturn(page);

        mockMvc.perform(get("/api/product/paged")
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title").value("Test Product"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(4))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.isLast").value(true));
    }
}
