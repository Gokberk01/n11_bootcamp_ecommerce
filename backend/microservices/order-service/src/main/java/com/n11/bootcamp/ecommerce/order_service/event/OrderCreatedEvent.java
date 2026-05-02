package com.n11.bootcamp.ecommerce.order_service.event;

import java.io.Serializable;
import java.util.List;

public class OrderCreatedEvent implements Serializable {
    private Long orderId;
    private String username;
    private Double totalPrice;
    private List<OrderCreatedEventItem> items;



    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public List<OrderCreatedEventItem> getItems() { return items; }
    public void setItems(List<OrderCreatedEventItem> items) { this.items = items; }
}
