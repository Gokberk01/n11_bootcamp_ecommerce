package com.n11.bootcamp.ecommerce.payment_service.dto.request;

public class CardDto {

    private String cardHolderName;
    private String cardNumber;
    private String expireMonth;
    private String expireYear;
    private String cvc;

    // getters / setters...
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
