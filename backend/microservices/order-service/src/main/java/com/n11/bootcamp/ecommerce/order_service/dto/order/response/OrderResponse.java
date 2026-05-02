package com.n11.bootcamp.ecommerce.order_service.dto.order.response;

import java.util.List;

public class OrderResponse {
    private Long orderId;
    private String username;
    private Double totalPrice;
    private String status;
    private List<OrderResponseItem> items;


    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OrderResponseItem> getItems() { return items; }
    public void setItems(List<OrderResponseItem> items) { this.items = items; }


}
