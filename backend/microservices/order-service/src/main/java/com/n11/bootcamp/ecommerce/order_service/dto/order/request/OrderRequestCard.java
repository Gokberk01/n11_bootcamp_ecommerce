package com.n11.bootcamp.ecommerce.order_service.dto.order.request;

public class OrderRequestCard {

    private String cardHolderName;
    private String cardNumber;
    private String expireMonth;
    private String expireYear;
    private String cvc;


    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpireMonth() { return expireMonth; }
    public void setExpireMonth(String expireMonth) { this.expireMonth = expireMonth; }

    public String getExpireYear() { return expireYear; }
    public void setExpireYear(String expireYear) { this.expireYear = expireYear; }

    public String getCvc() { return cvc; }
    public void setCvc(String cvc) { this.cvc = cvc; }
}
