package com.n11.bootcamp.ecommerce.order_service.dto.stock.request;

import java.io.Serializable;
import java.util.List;

public class StockReserveRequestedEvent implements Serializable {

    private Long orderId;
    private String username;
    private List<StockReserveRequestedEventItem> items;


    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public List<StockReserveRequestedEventItem> getItems() {
        return items;
    }
    public void setItems(List<StockReserveRequestedEventItem> items) {
        this.items = items;
    }


}
