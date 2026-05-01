package com.n11.bootcamp.ecommerce.payment_service.controller;


import com.n11.bootcamp.ecommerce.payment_service.dto.request.PaymentRequestDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.response.PaymentResponseDto;
import com.n11.bootcamp.ecommerce.payment_service.service.PaymentService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new PaymentRequestDto();
        requestDto.setOrderId(1L);
        requestDto.setUsername("user123");
    }

    @Test
    void makePayment_Success() {

        PaymentResponseDto successResponse = new PaymentResponseDto();
        successResponse.setSuccess(true);
        successResponse.setTransactionId("txn_123");

        when(paymentService.processPayment(any(PaymentRequestDto.class))).thenReturn(successResponse);

        ResponseEntity<PaymentResponseDto> response = paymentController.makePayment(requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("txn_123", response.getBody().getTransactionId());

        verify(paymentService, times(1)).processPayment(requestDto);
    }

    @Test
    void makePayment_Failure() {

        PaymentResponseDto failResponse = new PaymentResponseDto();
        failResponse.setSuccess(false);
        failResponse.setMessage("Insufficient funds");

        when(paymentService.processPayment(any(PaymentRequestDto.class))).thenReturn(failResponse);

        ResponseEntity<PaymentResponseDto> response = paymentController.makePayment(requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("Insufficient funds", response.getBody().getMessage());

        verify(paymentService, times(1)).processPayment(requestDto);
    }
}
