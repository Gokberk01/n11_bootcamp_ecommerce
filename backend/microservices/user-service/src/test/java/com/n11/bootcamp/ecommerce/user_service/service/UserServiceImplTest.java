package com.n11.bootcamp.ecommerce.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.n11.bootcamp.ecommerce.user_service.dto.request.LoginRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.SignUpRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.UpdateUserRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.response.JwtResponseDto;
import com.n11.bootcamp.ecommerce.user_service.entity.ShoppingCart;
import com.n11.bootcamp.ecommerce.user_service.entity.User;
import com.n11.bootcamp.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.n11.bootcamp.ecommerce.user_service.exception.UserNotFoundException;
import com.n11.bootcamp.ecommerce.user_service.repository.UserRepository;
import com.n11.bootcamp.ecommerce.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "jwtIssuerUri", "http://localhost:8080/token");
        ReflectionTestUtils.setField(userService, "jwtClientId", "test-client");
        ReflectionTestUtils.setField(userService, "jwtClientSecret", "test-secret");
        ReflectionTestUtils.setField(userService, "jwtGrantType", "password");
    }


    @Test
    void authenticateUser_Success() throws JsonProcessingException {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("kassadin");
        request.setPassword("password123");
        User user = new User("kassadin", "kass@void.com", "encodedPass", "Customer");
        user.setId(1L);

        when(userRepository.findByUsername("kassadin")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.authenticateUser(request);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof JwtResponseDto);
    }

    @Test
    void authenticateUser_UserNotFound_ThrowsException() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("unknown");
        request.setPassword("pass");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.authenticateUser(request));
    }


    @Test
    void registerUser_Success() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setUsername("newuser");
        request.setPassword("pass123");
        request.setEmail("new@mail.com");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        ResponseEntity<?> response = userService.registerUser(request);

        assertEquals(200, response.getStatusCode().value());
        verify(userRepository, times(1)).save(any(User.class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), any());
    }

    @Test
    void registerUser_UsernameExists_ThrowsException() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setUsername("exists");
        request.setPassword("pass");
        request.setEmail("mail@mail.com");
        when(userRepository.existsByUsername("exists")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
    }

    @Test
    void registerUser_EmailExists_ThrowsException() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setUsername("user");
        request.setPassword("pass");
        request.setEmail("exists@mail.com");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("exists@mail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
    }


    @Test
    void deleteUser_Success() {
        Long userId = 1L;
        User user = new User("kassadin", "kass@void.com", "pass", "Customer");
        user.setId(userId);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(100L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restTemplate.getForObject(anyString(), eq(ShoppingCart.class))).thenReturn(cart);

        ResponseEntity<?> response = userService.deleteUser(userId);

        assertEquals(200, response.getStatusCode().value());
        verify(restTemplate).delete(contains("100"));
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_RemoteCartError_StillDeletesUser() {
        Long userId = 1L;
        User user = new User("kassadin", "kass@void.com", "pass", "Customer");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restTemplate.getForObject(anyString(), eq(ShoppingCart.class))).thenThrow(new RuntimeException("Service Down"));

        ResponseEntity<?> response = userService.deleteUser(userId);

        assertEquals(200, response.getStatusCode().value());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }


    @Test
    void updateUser_FullUpdate_Success() {
        Long userId = 1L;
        User existingUser = new User("kassadin", "old@mail.com", "oldPass", "Customer");
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto();
        updateRequest.setEmail("new@mail.com");
        updateRequest.setPassword("newPass123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);

        ResponseEntity<?> response = userService.updateUser(userId, updateRequest);

        assertEquals(200, response.getStatusCode().value());
        verify(userRepository).save(existingUser);
        assertEquals("new@mail.com", existingUser.getEmail());
    }

    @Test
    void updateUser_EmailInUse_ThrowsException() {
        Long userId = 1L;
        User user = new User("kassadin", "old@mail.com", "pass", "Customer");
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setEmail("taken@mail.com");
        request.setPassword(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@mail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void updateUser_PartialUpdate_OnlyPassword() {
        Long userId = 1L;
        User user = new User("kassadin", "same@mail.com", "oldPass", "Customer");
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setEmail(null);
        request.setPassword("newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUser(userId, request);

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(user);
    }
}
