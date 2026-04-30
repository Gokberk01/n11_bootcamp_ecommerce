package com.n11.bootcamp.ecommerce.stock_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int productQuantity;

    public Stock(Long id, String productName, int productQuantity) {
        this.id = id;
        this.productName = productName;
        this.productQuantity = productQuantity;
    }

    public Stock() {}


    public void decreaseProductQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity < 0");
        if (productQuantity < quantity) throw new IllegalStateException("Insufficient stock");
        productQuantity -= quantity;
    }

    public void increaseProductQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity <= 0");
        productQuantity += quantity;
    }


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}


    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}


    public int getProductQuantity() {return productQuantity;}
    public void setProductQuantity(int productQuantity) {this.productQuantity = productQuantity;}
}
