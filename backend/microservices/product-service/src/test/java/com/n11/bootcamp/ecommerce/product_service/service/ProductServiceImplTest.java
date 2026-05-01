package com.n11.bootcamp.ecommerce.product_service.service;

import com.n11.bootcamp.ecommerce.product_service.entity.Product;
import com.n11.bootcamp.ecommerce.product_service.exception.InvalidCategoryKeyException;
import com.n11.bootcamp.ecommerce.product_service.repository.ProductRepository;
import com.n11.bootcamp.ecommerce.product_service.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
    }

    // ---------- getProductById ----------
    @Test
    void shouldReturnProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productService.getProductById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product, response.getBody());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound_getById() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.getProductById(1L));
    }

    // ---------- allProducts ----------
    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = productService.allProducts();

        assertEquals(1, response.getBody().size());
    }

    // ---------- createProduct ----------
    @Test
    void shouldCreateProduct() {
        when(productRepository.save(product)).thenReturn(product);

        ResponseEntity<Product> response = productService.createProduct(product);

        assertEquals(product, response.getBody());
    }

    // ---------- updateProduct ----------
    @Test
    void shouldUpdateProduct() {
        Product updated = new Product();
        updated.setTitle("Updated");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        ResponseEntity<Product> response = productService.updateProduct(1L, updated);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound_update() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.updateProduct(1L, new Product()));
    }

    // ---------- deleteProduct ----------
    @Test
    void shouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<String> response = productService.deleteProduct(1L);

        verify(productRepository).delete(product);
        assertTrue(response.getBody().contains("successfully"));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound_delete() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(1L));
    }

    // ---------- deleteAllProducts ----------
    @Test
    void shouldDeleteAllProducts() {
        ResponseEntity<String> response = productService.deleteAllProducts();

        verify(productRepository).deleteAll();
        assertTrue(response.getBody().contains("All Products"));
    }

    // ---------- uploadImage ----------
    @Test
    void shouldUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "dummy".getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        Product result = productService.uploadImage(1L, file);

        assertNotNull(result);
        assertNotNull(result.getImg());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound_uploadImage() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "dummy".getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.uploadImage(1L, file));
    }

    // ---------- getPaged ----------
    @Test
    void shouldReturnPagedProducts() {
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        Page<Product> result = productService.getPaged(0, 10);

        assertEquals(1, result.getContent().size());
    }

    // ---------- handleCategoryKeyChange ----------
    @Test
    void shouldUpdateCategoryKey() {
        when(productRepository.updateCategoryKeyForProducts("old", "new"))
                .thenReturn(5);

        ResponseEntity<String> response =
                productService.handleCategoryKeyChange("old", "new");

        assertTrue(response.getBody().contains("Updated 5"));
    }

    @Test
    void shouldReturnBadRequestWhenKeysInvalid() {
        assertThrows(InvalidCategoryKeyException.class, () ->
                productService.handleCategoryKeyChange(null, "new")
        );
    }

    @Test
    void shouldHandleExceptionInCategoryUpdate() {
        when(productRepository.updateCategoryKeyForProducts(any(), any()))
                .thenThrow(new RuntimeException());

        ResponseEntity<String> response =
                productService.handleCategoryKeyChange("old", "new");

        assertEquals(500, response.getStatusCodeValue());
    }
}
