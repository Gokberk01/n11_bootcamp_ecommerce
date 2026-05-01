package com.n11.bootcamp.ecommerce.order_service.service;

import com.n11.bootcamp.ecommerce.order_service.dto.payment.PaymentRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.payment.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-service") // Eureka üzerinden discovery
public interface PaymentServiceClient {

    @PostMapping("/api/payment/pay")
    PaymentResponse makePayment(PaymentRequest request);

}
