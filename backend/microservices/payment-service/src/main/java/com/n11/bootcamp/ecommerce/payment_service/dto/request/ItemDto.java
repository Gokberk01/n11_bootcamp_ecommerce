package com.n11.bootcamp.ecommerce.payment_service.dto.request;

import java.math.BigDecimal;

public class ItemDto {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String category1;
    private String category2;

    // getters / setters...
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getCategory1() { return category1; }
    public void setCategory1(String category1) { this.category1 = category1; }
    public String getCategory2() { return category2; }
    public void setCategory2(String category2) { this.category2 = category2; }
}
