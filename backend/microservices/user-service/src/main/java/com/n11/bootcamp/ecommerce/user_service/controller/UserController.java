package com.n11.bootcamp.ecommerce.user_service.controller;

import com.n11.bootcamp.ecommerce.user_service.dto.request.LoginRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.SignUpRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.UpdateUserRequestDto;
import com.n11.bootcamp.ecommerce.user_service.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {this.userServiceImpl = userServiceImpl;}


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        return userServiceImpl.authenticateUser(loginRequest);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        return userServiceImpl.registerUser(signUpRequest);
    }


    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        return userServiceImpl.deleteUser(userId);
    }


    @PutMapping("/update/{userId}")

    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @Valid @RequestBody UpdateUserRequestDto updateUserRequest) {
        return userServiceImpl.updateUser(userId, updateUserRequest);
    }


}
