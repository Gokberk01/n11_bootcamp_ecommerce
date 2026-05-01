package com.n11.bootcamp.ecommerce.payment_service.service;


import com.iyzipay.Options;
import com.iyzipay.model.Payment;
import com.iyzipay.request.CreatePaymentRequest;
import com.n11.bootcamp.ecommerce.payment_service.dto.request.CardDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.request.ItemDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.request.PaymentRequestDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.response.PaymentResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private Options options;

    @Mock
    private Payment paymentMock;

    @InjectMocks
    private PaymentService paymentService;

    private MockedStatic<Payment> mockedPayment;
    private PaymentRequestDto requestDto;

    @BeforeEach
    void setUp() {

        mockedPayment = mockStatic(Payment.class);

        requestDto = new PaymentRequestDto();
        requestDto.setOrderId(100L);
        requestDto.setUsername("gokberkozkan");
        requestDto.setAmount(BigDecimal.valueOf(100));
        requestDto.setFirstName("Gokberk");
        requestDto.setLastName("Ozkan");
        requestDto.setEmail("gokberkzkn@hotmail.com");
        requestDto.setPhone("5555555555");
        requestDto.setAddress("Void Street");

        CardDto cardDto = new CardDto();
        cardDto.setCardHolderName("Gokberk Ozkan");
        cardDto.setCardNumber("1234123412341234");
        cardDto.setExpireMonth("12");
        cardDto.setExpireYear("2030");
        cardDto.setCvc("123");
        requestDto.setCard(cardDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setProductId(1L);
        itemDto.setProductName("Phone");
        itemDto.setPrice(BigDecimal.valueOf(100));
        itemDto.setQuantity(1);
        requestDto.setItems(List.of(itemDto));
    }

    @AfterEach
    void tearDown() {
        mockedPayment.close();
    }

    @Test
    void processPayment_Success() {

        when(paymentMock.getStatus()).thenReturn("success");
        when(paymentMock.getPaymentId()).thenReturn("12345");
        mockedPayment.when(() -> Payment.create(any(CreatePaymentRequest.class), any(Options.class)))
                .thenReturn(paymentMock);

        PaymentResponseDto response = paymentService.processPayment(requestDto);

        assertTrue(response.isSuccess());
        assertEquals("12345", response.getTransactionId());
        assertNull(response.getMessage());
    }

    @Test
    void processPayment_Failure() {
        when(paymentMock.getStatus()).thenReturn("failure");
        when(paymentMock.getErrorMessage()).thenReturn("Insufficient Funds");
        when(paymentMock.getErrorCode()).thenReturn("5051");
        mockedPayment.when(() -> Payment.create(any(CreatePaymentRequest.class), any(Options.class)))
                .thenReturn(paymentMock);

        PaymentResponseDto response = paymentService.processPayment(requestDto);

        assertFalse(response.isSuccess());
        assertEquals("Insufficient Funds", response.getMessage());
    }

    @Test
    void processPayment_Exception() {

        mockedPayment.when(() -> Payment.create(any(CreatePaymentRequest.class), any(Options.class)))
                .thenThrow(new RuntimeException("Connection Timeout"));

        PaymentResponseDto response = paymentService.processPayment(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Connection Timeout"));
    }

    @Test
    void processPayment_AlternativeAddress() {

        requestDto.setAddress(null);
        requestDto.setStreetAddress("Alternative Street");
        requestDto.setCity(null);
        requestDto.setCountry(null);

        when(paymentMock.getStatus()).thenReturn("success");
        mockedPayment.when(() -> Payment.create(any(CreatePaymentRequest.class), any(Options.class)))
                .thenReturn(paymentMock);

        PaymentResponseDto response = paymentService.processPayment(requestDto);

        assertTrue(response.isSuccess());
    }
}
