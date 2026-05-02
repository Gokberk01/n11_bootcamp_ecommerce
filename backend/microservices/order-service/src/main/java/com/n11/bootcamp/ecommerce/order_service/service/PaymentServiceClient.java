package com.n11.bootcamp.ecommerce.order_service.service;

import com.n11.bootcamp.ecommerce.order_service.dto.payment.request.PaymentRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.payment.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payment/pay")
    PaymentResponse makePayment(PaymentRequest request);

}
