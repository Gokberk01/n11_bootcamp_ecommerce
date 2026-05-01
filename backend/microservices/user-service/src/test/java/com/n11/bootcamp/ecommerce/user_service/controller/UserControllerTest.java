package com.n11.bootcamp.ecommerce.user_service.controller;

import com.n11.bootcamp.ecommerce.user_service.dto.request.LoginRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.SignUpRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.UpdateUserRequestDto;
import com.n11.bootcamp.ecommerce.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private UserController userController;

    private LoginRequestDto loginRequestDto;
    private SignUpRequestDto signUpRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("kassadin");
        loginRequestDto.setPassword("void123");

        signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("kassadin");
        signUpRequestDto.setEmail("kassadin@void.com");
        signUpRequestDto.setPassword("void123");

        updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setEmail("updated@void.com");
        updateUserRequestDto.setPassword("newVoid123");
    }

    @Test
    void authenticateUser_Success() {

        when(userServiceImpl.authenticateUser(any(LoginRequestDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> response = userController.authenticateUser(loginRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userServiceImpl, times(1)).authenticateUser(loginRequestDto);
    }

    @Test
    void registerUser_Success() {

        when(userServiceImpl.registerUser(any(SignUpRequestDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        ResponseEntity<?> response = userController.registerUser(signUpRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userServiceImpl, times(1)).registerUser(signUpRequestDto);
    }

    @Test
    void deleteUser_Success() {

        Long userId = 1L;
        when(userServiceImpl.deleteUser(eq(userId)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userServiceImpl, times(1)).deleteUser(userId);
    }

    @Test
    void updateUser_Success() {

        Long userId = 1L;
        when(userServiceImpl.updateUser(eq(userId), any(UpdateUserRequestDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<?> response = userController.updateUser(userId, updateUserRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userServiceImpl, times(1)).updateUser(userId, updateUserRequestDto);
    }

    @Test
    void deleteUser_NotFound() {

        Long userId = 99L;
        when(userServiceImpl.deleteUser(eq(userId)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userServiceImpl, times(1)).deleteUser(userId);
    }
}
