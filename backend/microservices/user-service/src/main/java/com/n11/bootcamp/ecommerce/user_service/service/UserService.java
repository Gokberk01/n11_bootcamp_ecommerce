package com.n11.bootcamp.ecommerce.user_service.service;

import com.n11.bootcamp.ecommerce.user_service.dto.request.LoginRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.SignUpRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.UpdateUserRequestDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> authenticateUser(LoginRequestDto loginRequestDto);
    ResponseEntity<?> registerUser(SignUpRequestDto signUpRequest);
    ResponseEntity<?> deleteUser(Long userId);
    ResponseEntity<?> updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto);
}
