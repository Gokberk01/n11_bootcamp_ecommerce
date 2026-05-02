package com.n11.bootcamp.ecommerce.order_service.dto.stock.response;

public class StockUpdateResponseItem {

    private Long productId;
    private Integer oldQuantity;
    private Integer newQuantity;


    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getOldQuantity() { return oldQuantity; }
    public void setOldQuantity(Integer oldQuantity) { this.oldQuantity = oldQuantity; }

    public Integer getNewQuantity() { return newQuantity; }
    public void setNewQuantity(Integer newQuantity) { this.newQuantity = newQuantity; }
}
