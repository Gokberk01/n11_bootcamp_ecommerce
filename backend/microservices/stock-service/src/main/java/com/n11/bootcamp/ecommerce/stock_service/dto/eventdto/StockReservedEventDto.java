package com.n11.bootcamp.ecommerce.stock_service.dto.eventdto;

public class StockReservedEventDto {

    private Long orderId;
    private String username;
    private String message;

    public StockReservedEventDto() { }

    public StockReservedEventDto(Long orderId, String username, String message) {
        this.orderId = orderId;
        this.username = username;
        this.message = message;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}
