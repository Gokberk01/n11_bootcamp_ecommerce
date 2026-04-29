package com.n11.bootcamp.ecommerce.product_service.controller;

import com.n11.bootcamp.ecommerce.product_service.entity.Product;
import com.n11.bootcamp.ecommerce.product_service.service.impl.ProductServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/product")
public class ProductController  {

    private final ProductServiceImpl productServiceImpl;

    public ProductController(ProductServiceImpl productServiceImpl)
    {
        this.productServiceImpl = productServiceImpl;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return productServiceImpl.createProduct(product);
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long productId,
                                                 @RequestBody Product updatedProduct)
    {
        return productServiceImpl.updateProduct(productId, updatedProduct);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long productId) {
        return productServiceImpl.deleteProduct(productId);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll() {
        return productServiceImpl.deleteAllProducts();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return productServiceImpl.allProducts();
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Page<Product> productPage = productServiceImpl.getPaged(page, size);
        return ResponseEntity.ok(Map.of(
                "items", productPage.getContent(),
                "page", productPage.getNumber(),
                "size", productPage.getSize(),
                "totalElements", productPage.getTotalElements(),
                "totalPages", productPage.getTotalPages(),
                "isLast", productPage.isLast()
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long productId) {
        return productServiceImpl.getProductById(productId);
    }
}
