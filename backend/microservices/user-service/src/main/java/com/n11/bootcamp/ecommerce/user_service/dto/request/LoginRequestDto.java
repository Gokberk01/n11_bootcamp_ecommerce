package com.n11.bootcamp.ecommerce.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequestDto {

    @NotBlank(message = "Invalid Username: Empty username")
    private String username;

    @NotBlank(message = "Invalid Password: Empty password")
    private String password;



    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}


    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

}
