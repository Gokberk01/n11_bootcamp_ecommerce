package com.n11.bootcamp.ecommerce.order_service.dto.payment.request;

public class PaymentRequestItem {

    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String category1;
    private String category2;



    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCategory1() { return category1; }
    public void setCategory1(String category1) { this.category1 = category1; }

    public String getCategory2() { return category2; }
    public void setCategory2(String category2) { this.category2 = category2; }
}