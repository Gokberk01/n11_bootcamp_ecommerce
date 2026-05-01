package com.n11.bootcamp.ecommerce.shopping_cart_service.repository;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);
}
