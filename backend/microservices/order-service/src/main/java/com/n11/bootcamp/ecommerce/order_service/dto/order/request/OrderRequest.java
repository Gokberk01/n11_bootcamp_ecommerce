package com.n11.bootcamp.ecommerce.order_service.dto.order.request;

import java.util.List;


public class OrderRequest {

    private String username;
    private List<OrderRequestItem> items;

    private String firstName;
    private String lastName;
    private String streetAddress;
    private String city;
    private String country;
    private String phone;
    private String email;

    private String paymentMethod;
    private OrderRequestCard card;



    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<OrderRequestItem> getItems() { return items; }
    public void setItems(List<OrderRequestItem> items) { this.items = items; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public OrderRequestCard getCard() { return card; }
    public void setCard(OrderRequestCard card) { this.card = card; }

}
