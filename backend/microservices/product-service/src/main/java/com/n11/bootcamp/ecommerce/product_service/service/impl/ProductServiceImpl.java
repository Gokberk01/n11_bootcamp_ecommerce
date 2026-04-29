package com.n11.bootcamp.ecommerce.product_service.service.impl;

import com.n11.bootcamp.ecommerce.product_service.entity.Product;
import com.n11.bootcamp.ecommerce.product_service.repository.ProductRepository;
import com.n11.bootcamp.ecommerce.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ResponseEntity<Product> getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Cannot get ProductById. Product does not exist in DB"));
        return ResponseEntity.ok(product);
    }

    @Override
    public ResponseEntity<List<Product>> allProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @Override
    public ResponseEntity<Product> createProduct(Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    @Override
    public ResponseEntity<Product> updateProduct(Long productId, Product updatedProduct) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Cannot update ProductById. Product does not exist in DB"));

        product.setBrand(updatedProduct.getBrand());
        product.setCategory(updatedProduct.getCategory());
        product.setColor(updatedProduct.getColor());
        product.setImg(updatedProduct.getImg());
        product.setCategoryKey(updatedProduct.getCategoryKey());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setTitle(updatedProduct.getTitle());


        return ResponseEntity.ok(productRepository.save(product));
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Cannot delete ProductById. Product does not exist in DB"));
        productRepository.delete(product);
        return ResponseEntity.ok("Delete ProductById successfully");
    }

    @Override
    public ResponseEntity<String> deleteAllProducts() {
        productRepository.deleteAll();
        return ResponseEntity.ok("All Products deleted successfully");
    }

    @Override
    public Product uploadImage(Long productId, MultipartFile file) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Cannot find ProductById. Cannot upload image. Product does not exist in DB."));

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("./images/products", fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        product.setImg(fileName);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Page<Product> getPaged(int pageNumber, int pageSize) {
        return productRepository.findAll(
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"))
        );
    }

    @Override
    public ResponseEntity<String> handleCategoryKeyChange(String oldKey, String newKey) {
        try {
            if (oldKey == null || newKey == null || oldKey.equalsIgnoreCase(newKey)) {
                return ResponseEntity.badRequest().body("Category key update ignored (null or same): " + oldKey +  " -> "  + newKey);
            }
            int updatedCount = productRepository.updateCategoryKeyForProducts(oldKey, newKey);
            return ResponseEntity.ok("Updated " + updatedCount + " products: categoryKey '"+ oldKey +"' -> '" + newKey + "'");
        } catch (Exception ex) {
            String errorMessage = String.format("Failed to update product categoryKeys for  '%s' -> '%s'",
                    oldKey, newKey);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }
}
