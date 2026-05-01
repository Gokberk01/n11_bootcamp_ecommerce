package com.n11.bootcamp.ecommerce.product_service.service.impl;

import com.n11.bootcamp.ecommerce.product_service.entity.Product;
import com.n11.bootcamp.ecommerce.product_service.exception.InvalidCategoryKeyException;
import com.n11.bootcamp.ecommerce.product_service.exception.ProductNotFoundException;
import com.n11.bootcamp.ecommerce.product_service.repository.ProductRepository;
import com.n11.bootcamp.ecommerce.product_service.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ResponseEntity<Product> getProductById(Long productId) {
        LOGGER.info("Fetching product by id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        LOGGER.debug("Product found: {}", product);
        return ResponseEntity.ok(product);
    }

    @Override
    public ResponseEntity<List<Product>> allProducts() {
        LOGGER.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        LOGGER.debug("Total products fetched: {}", products.size());
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<Product> createProduct(Product product) {
        LOGGER.info("Creating product with title: {}", product.getTitle());
        Product saved = productRepository.save(product);
        LOGGER.debug("Product created with id: {}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<Product> updateProduct(Long productId, Product updatedProduct) {
        LOGGER.info("Updating product id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    LOGGER.error("Product not found for update: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        product.setBrand(updatedProduct.getBrand());
        product.setCategory(updatedProduct.getCategory());
        product.setColor(updatedProduct.getColor());
        product.setImg(updatedProduct.getImg());
        product.setCategoryKey(updatedProduct.getCategoryKey());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setTitle(updatedProduct.getTitle());

        Product saved = productRepository.save(product);
        LOGGER.debug("Product updated successfully: {}", saved.getId());

        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long productId) {
        LOGGER.warn("Deleting product id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    LOGGER.error("Product not found for deletion: {}", productId);
                    return new ProductNotFoundException(productId);
                });
        productRepository.delete(product);
        LOGGER.info("Product deleted: {}", productId);
        return ResponseEntity.ok("Delete ProductById successfully");
    }

    @Override
    public ResponseEntity<String> deleteAllProducts() {
        LOGGER.warn("Deleting ALL products");
        productRepository.deleteAll();
        LOGGER.info("All products deleted");
        return ResponseEntity.ok("All Products deleted successfully");
    }

    @Override
    public Product uploadImage(Long productId, MultipartFile file) throws Exception {
        LOGGER.info("Uploading image for product id: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    LOGGER.error("Product not found for image upload: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("./images/products", fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        product.setImg(fileName);
        LOGGER.info("Image uploaded for product id: {}, file: {}", productId, fileName);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Page<Product> getPaged(int pageNumber, int pageSize) {
        LOGGER.info("Fetching paged products: page={}, size={}", pageNumber, pageSize);
        Page<Product> page = productRepository.findAll(
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"))
        );

        LOGGER.debug("Page fetched: totalElements={}", page.getTotalElements());
        return page;
    }

    @Override
    public ResponseEntity<String> handleCategoryKeyChange(String oldKey, String newKey) {
        LOGGER.info("Updating categoryKey from '{}' to '{}'", oldKey, newKey);
        if (oldKey == null || newKey == null || oldKey.equalsIgnoreCase(newKey)) {
            LOGGER.warn("Invalid categoryKey update request: {} -> {}", oldKey, newKey);
            throw new InvalidCategoryKeyException(oldKey, newKey);
        }

        try {

            int updatedCount = productRepository.updateCategoryKeyForProducts(oldKey, newKey);
            LOGGER.info("CategoryKey updated for {} products", updatedCount);

            return ResponseEntity.ok("Updated " + updatedCount + " products: categoryKey '"+ oldKey +"' -> '" + newKey + "'");

        } catch (Exception ex) {
            
            LOGGER.error("Failed to update categoryKey: {} -> {}", oldKey, newKey, ex);
            String errorMessage = String.format("Failed to update product categoryKeys for  '%s' -> '%s'",
                    oldKey, newKey);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }
}
