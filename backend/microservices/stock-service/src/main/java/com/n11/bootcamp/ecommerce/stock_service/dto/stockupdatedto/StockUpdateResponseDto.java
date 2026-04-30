package com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto;

public class StockUpdateResponseDto {

    private boolean success;
    private String message;

    public StockUpdateResponseDto() { }

    public StockUpdateResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }


    public static StockUpdateResponseDto ok(String msg) {
        return new StockUpdateResponseDto(true, msg);
    }

    public static StockUpdateResponseDto fail(String msg) {
        return new StockUpdateResponseDto(false, msg);
    }

}
