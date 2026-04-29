package com.n11.bootcamp.ecommerce.shopping_cart_service.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String shoppingCartName;

    @ManyToMany
    @JoinTable(
            name = "shopping_cart_product",
            joinColumns = @JoinColumn(name = "shopping_cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;


    // Getters - Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getShoppingCartName() { return shoppingCartName; }
    public void setShoppingCartName(String shoppingCartName) { this.shoppingCartName = shoppingCartName; }

    public Set<Product> getProducts() { return products; }
    public void setProducts(Set<Product> products) { this.products = products; }
}
