package com.n11.bootcamp.ecommerce.shopping_cart_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Product {

    @Id
    private long id;

    private String title;
    private String img;
    private String brand;
    private long price;
    private String category;

    @Column(name = "category_key")
    private String categoryKey;

    @ManyToMany(mappedBy = "products")
    private Set<ShoppingCart> shoppingCarts;

    @PrePersist
    @PreUpdate
    private void fillRequiredFields() {

        if ((category == null || category.isBlank()) &&
                (categoryKey != null && !categoryKey.isBlank())) {
            category = categoryKey;
        }

        if (title == null || title.isBlank()) {
            title = "product-" + id;
        }

        if (category == null || category.isBlank()) {
            category = "unknown";
        }
    }


    // Getters - Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

    @JsonIgnore
    public Set<ShoppingCart> getShoppingCarts() { return shoppingCarts; }
    public void setShoppingCarts(Set<ShoppingCart> shoppingCarts) { this.shoppingCarts = shoppingCarts; }

}
