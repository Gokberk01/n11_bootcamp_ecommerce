package com.n11.bootcamp.ecommerce.user_service.service;

import com.n11.bootcamp.ecommerce.user_service.dto.request.LoginRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.SignUpRequestDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> authenticateUser(LoginRequestDto loginRequestDto);
    ResponseEntity<?> registerUser(SignUpRequestDto signUpRequest);
}
