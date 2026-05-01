package com.n11.bootcamp.ecommerce.payment_service.controller;

import com.n11.bootcamp.ecommerce.payment_service.dto.request.PaymentRequestDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.response.PaymentResponseDto;
import com.n11.bootcamp.ecommerce.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
//@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService)
    {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> makePayment(@RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.processPayment(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }
}
