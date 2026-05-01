package com.n11.bootcamp.ecommerce.payment_service.dto.response;

public class PaymentResponseDto {

    private boolean success;
    private String transactionId;
    private String message;


    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
