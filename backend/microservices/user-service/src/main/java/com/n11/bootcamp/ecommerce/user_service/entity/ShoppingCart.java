package com.n11.bootcamp.ecommerce.user_service.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class ShoppingCart {

    @Id
    private long id;
    private String shoppingCartName;


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
