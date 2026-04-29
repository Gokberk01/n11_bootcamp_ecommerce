package com.n11.bootcamp.ecommerce.product_service.service;

import com.n11.bootcamp.ecommerce.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ResponseEntity<Product> getProductById(Long productId);

    ResponseEntity<List<Product>> allProducts();

    ResponseEntity<Product> createProduct(Product product);

    ResponseEntity<Product> updateProduct(Long productId, Product updatedProduct);

    ResponseEntity<String> deleteProduct(Long id);

    ResponseEntity<String> deleteAllProducts();

    Product uploadImage(Long id, MultipartFile file) throws Exception;

    Page<Product> getPaged(int page, int size);

    //void handleCategoryKeyChange(String oldKey, String newKey);
    ResponseEntity<String> handleCategoryKeyChange(String oldKey, String newKey);
}
