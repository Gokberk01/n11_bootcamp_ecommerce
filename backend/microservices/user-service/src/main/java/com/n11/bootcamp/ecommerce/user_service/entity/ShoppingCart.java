package com.n11.bootcamp.ecommerce.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.Set;

@Entity
public class ShoppingCart {

    @Id
    private long id;
    private String shoppingCartName;

    @ManyToMany(mappedBy = "products")
    private Set<ShoppingCart> shoppingCarts;


    // Getters - Setters

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }


    public String getShoppingCartName() {
        return shoppingCartName;
    }
    public void setShoppingCartName(String shoppingCartName) {
        this.shoppingCartName = shoppingCartName;
    }

}
