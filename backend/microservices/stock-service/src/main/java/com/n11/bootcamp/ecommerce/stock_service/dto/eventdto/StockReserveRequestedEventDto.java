package com.n11.bootcamp.ecommerce.stock_service.dto.eventdto;

import java.util.List;

public class StockReserveRequestedEventDto {

    private Long orderId;
    private String username;
    private List<ItemDto> items;

    public StockReserveRequestedEventDto() { }

    public StockReserveRequestedEventDto(Long orderId, String username, List<ItemDto> items) {
        this.orderId = orderId;
        this.username = username;
        this.items = items;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<ItemDto> getItems() { return items; }
    public void setItems(List<ItemDto> items) { this.items = items; }
}
