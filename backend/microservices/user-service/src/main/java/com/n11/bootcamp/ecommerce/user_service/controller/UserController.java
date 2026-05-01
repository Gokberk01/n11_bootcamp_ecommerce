package com.n11.bootcamp.ecommerce.user_service.controller;

import com.n11.bootcamp.ecommerce.user_service.dto.request.LoginRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.SignUpRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.UpdateUserRequestDto;
import com.n11.bootcamp.ecommerce.user_service.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "User Authentication and Profile Management APIs")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {this.userServiceImpl = userServiceImpl;}

    @Operation(summary = "Login user", description = "Authenticates a user and returns a token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        LOGGER.info("API CALL: Sign in request for user: {}", loginRequest.getUsername());
        return userServiceImpl.authenticateUser(loginRequest);
    }

    @Operation(summary = "Register new user", description = "Creates a new user account in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "User already exists or invalid data")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        LOGGER.info("API CALL: Sign up request with username, email and password: {} , {} , {}", signUpRequest.getUsername() , signUpRequest.getEmail() , signUpRequest.getPassword());
        return userServiceImpl.registerUser(signUpRequest);
    }

    @Operation(summary = "Delete user", description = "Removes a user account from the system by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        LOGGER.warn("API CALL: Delete user request for ID: {}", userId);
        return userServiceImpl.deleteUser(userId);
    }

    @Operation(summary = "Update user profile", description = "Updates the profile information of an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @Valid @RequestBody UpdateUserRequestDto updateUserRequest) {
        LOGGER.info("API CALL: Update user request for ID: {}", userId);
        return userServiceImpl.updateUser(userId, updateUserRequest);
    }


}
