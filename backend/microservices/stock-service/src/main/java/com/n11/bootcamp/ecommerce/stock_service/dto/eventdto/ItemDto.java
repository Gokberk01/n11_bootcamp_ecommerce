package com.n11.bootcamp.ecommerce.stock_service.dto.eventdto;

public class ItemDto {

    private Long productId;
    private Integer quantity;

    public ItemDto() { }

    public ItemDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

}
