package com.n11.bootcamp.ecommerce.order_service.dto.stock.request;

import java.io.Serializable;

public class StockReserveRequestedEventItem implements Serializable {

    private Long productId;
    private Integer quantity;


    public StockReserveRequestedEventItem() {}

    public StockReserveRequestedEventItem(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }


    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
