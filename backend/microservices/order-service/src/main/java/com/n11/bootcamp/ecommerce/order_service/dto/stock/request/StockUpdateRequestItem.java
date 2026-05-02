package com.n11.bootcamp.ecommerce.order_service.dto.stock.request;

public class StockUpdateRequestItem {

    private Long productId;
    private Integer quantity;


    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
