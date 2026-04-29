package com.n11.bootcamp.ecommerce.product_service.entity;


import jakarta.persistence.*;

@Entity
@Table(
        name = "product",
        indexes = {
                @Index(name = "idx_products_brand", columnList = "brand"),
                @Index(name = "idx_products_color", columnList = "color")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "img", length = 512)
    private String img;

    @Column(name = "color")
    private String color;

    @Column(name = "brand")
    private String brand;

    @Column(name = "category", nullable = false)
    private String category = "Electronic";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "title", nullable = false)
    private String title = "-";

    @Column(name = "categoryKey")
    private String categoryKey;


    // Getters - Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }
}
