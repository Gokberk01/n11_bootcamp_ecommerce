package com.n11.bootcamp.ecommerce.user_service.dto.response;

public class MessageResponseDto {

    private String message;


    public MessageResponseDto(String message) {this.message = message;}



    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
}
