package com.n11.bootcamp.ecommerce.payment_service.controller;

import com.n11.bootcamp.ecommerce.payment_service.dto.request.PaymentRequestDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.response.PaymentResponseDto;
import com.n11.bootcamp.ecommerce.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment Management", description = "APIs for processing financial transactions and payment validation")
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService)
    {
        this.paymentService = paymentService;
    }


    @Operation(
            summary = "Process payment",
            description = "Validates credit card information to process the order payment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment successfully processed"),
            @ApiResponse(responseCode = "402", description = "Payment required - Insufficient funds or invalid card"),
            @ApiResponse(responseCode = "500", description = "Internal server error during payment gateway communication")
    })
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> makePayment(@RequestBody PaymentRequestDto request) {
        LOGGER.info("API CALL: Payment request received for Order ID: {} and User: {}",
                request.getOrderId(), request.getUsername());

        PaymentResponseDto response = paymentService.processPayment(request);
        if (response.isSuccess()) {
            LOGGER.info("API SUCCESS: Payment approved for Order ID: {}", request.getOrderId());
            return ResponseEntity.ok(response);
        }

        LOGGER.warn("API WARN: Payment rejected for Order ID: {}. Reason: {}",
                request.getOrderId(), response.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }
}
