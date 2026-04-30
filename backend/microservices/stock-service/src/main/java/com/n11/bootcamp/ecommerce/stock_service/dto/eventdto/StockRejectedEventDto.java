package com.n11.bootcamp.ecommerce.stock_service.dto.eventdto;

public class StockRejectedEventDto {
    private Long orderId;
    private String username;
    private String reason;

    public StockRejectedEventDto() { }

    public StockRejectedEventDto(Long orderId, String username, String reason) {
        this.orderId = orderId;
        this.username = username;
        this.reason = reason;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

}
