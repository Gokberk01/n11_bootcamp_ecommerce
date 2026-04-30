package com.n11.bootcamp.ecommerce.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private ShoppingCart shoppingCart;
}