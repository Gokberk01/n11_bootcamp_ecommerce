package com.n11.bootcamp.ecommerce.order_service.dto.payment.request;

import java.util.List;

public class PaymentRequest {

    private Long orderId;
    private String username;
    private Double amount;
    private String paymentMethod;


    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String streetAddress;
    private String country;
    private String address;
    private PaymentRequestCard card;
    private List<PaymentRequestItem> items;



    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public PaymentRequestCard getCard() { return card; }
    public void setCard(PaymentRequestCard card) { this.card = card; }

    public List<PaymentRequestItem> getItems() { return items; }
    public void setItems(List<PaymentRequestItem> items) { this.items = items; }




}