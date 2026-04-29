package com.n11.bootcamp.ecommerce.shopping_cart_service.repository;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @EntityGraph(attributePaths = {"products"})
    Optional<ShoppingCart> findById(Long id);

    @EntityGraph(attributePaths = {"products"})
    Optional<ShoppingCart> findByShoppingCartName(String shoppingCartName);
}
