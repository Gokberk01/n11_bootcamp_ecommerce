package com.n11.bootcamp.ecommerce.product_service.controller;

import com.n11.bootcamp.ecommerce.product_service.entity.Product;
import com.n11.bootcamp.ecommerce.product_service.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
@Tag(name = "Product", description = "Product CRUD and Management APIs")
public class ProductController  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductServiceImpl productServiceImpl;

    public ProductController(ProductServiceImpl productServiceImpl)
    {
        this.productServiceImpl = productServiceImpl;
    }

    @Operation(summary = "Create a new product", description = "Saves a new product to the database and returns the saved entity.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        LOGGER.info("API CALL: Create product");
        return productServiceImpl.createProduct(product);
    }

    @Operation(summary = "Update an existing product", description = "Updates the product details based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully updated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long productId,
                                                 @RequestBody Product updatedProduct)
    {
        LOGGER.info("API CALL: Update product id={}", productId);
        return productServiceImpl.updateProduct(productId, updatedProduct);
    }

    @Operation(summary = "Delete product by ID")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long productId) {
        LOGGER.warn("API CALL: Delete product id={}", productId);
        return productServiceImpl.deleteProduct(productId);
    }

    @Operation(summary = "Delete all products", description = "Danger: This will remove all products from the database.")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll() {
        LOGGER.warn("API CALL: Delete ALL products");
        return productServiceImpl.deleteAllProducts();
    }

    @Operation(summary = "Get all products", description = "Retrieves a full list of products without pagination.")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        LOGGER.info("API CALL: Get all products");
        return productServiceImpl.allProducts();
    }

    @Operation(summary = "Get paginated products", description = "Retrieves products in a paginated format with total count and page info.")
    @GetMapping("/paged")
    public ResponseEntity<?> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        LOGGER.info("API CALL: Get paged products page={}, size={}", page, size);
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

    @Operation(summary = "Get product by ID", description = "Returns a single product object based on its unique ID.")
    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long productId) {
        LOGGER.info("API CALL: Get product id={}", productId);
        return productServiceImpl.getProductById(productId);
    }
}
