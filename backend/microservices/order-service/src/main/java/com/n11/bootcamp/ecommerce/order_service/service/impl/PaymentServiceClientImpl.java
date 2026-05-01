package com.n11.bootcamp.ecommerce.order_service.service.impl;

import com.n11.bootcamp.ecommerce.order_service.dto.payment.PaymentRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.payment.PaymentResponse;
import com.n11.bootcamp.ecommerce.order_service.service.PaymentServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServiceClientImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceClientImpl.class);

    private final PaymentServiceClient paymentServiceClient;

    public PaymentServiceClientImpl(PaymentServiceClient paymentServiceClient)
    {
        this.paymentServiceClient = paymentServiceClient;
    }

    /**
     * Ödeme servisine istek gönderir
     * @param request PaymentRequest DTO
     * @return PaymentResponse DTO
     */

    public PaymentResponse processPayment(PaymentRequest request)
    {
        try {
            return paymentServiceClient.makePayment(request);
        } catch (Exception e) {
            LOGGER.error("Payment service call failed: {}", e.getMessage());
            throw new RuntimeException("Payment service failed", e);
        }
    }
}
