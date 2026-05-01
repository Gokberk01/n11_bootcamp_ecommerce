package com.n11.bootcamp.ecommerce.payment_service.service;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.n11.bootcamp.ecommerce.payment_service.dto.request.PaymentRequestDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.response.PaymentResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private final Options options;

    public PaymentService(Options options)
    {
        this.options = options;
    }

    public PaymentResponseDto processPayment(PaymentRequestDto request) {

        LOGGER.info("PAYMENT PROCESS START: Starting transaction for Order ID: {} and User: {}",
                request.getOrderId(), request.getUsername());


        CreatePaymentRequest iyzicoRequest = new CreatePaymentRequest();

        iyzicoRequest.setPrice(request.getAmount());
        iyzicoRequest.setPaidPrice(request.getAmount());
        iyzicoRequest.setCurrency(Currency.TRY.name());
        iyzicoRequest.setBasketId(request.getOrderId().toString());
        iyzicoRequest.setPaymentChannel(PaymentChannel.WEB.name());
        iyzicoRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());


        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(request.getCard().getCardHolderName());
        paymentCard.setCardNumber(request.getCard().getCardNumber());
        paymentCard.setExpireMonth(request.getCard().getExpireMonth());
        paymentCard.setExpireYear(request.getCard().getExpireYear());
        paymentCard.setCvc(request.getCard().getCvc());
        paymentCard.setRegisterCard(0);
        iyzicoRequest.setPaymentCard(paymentCard);


        Buyer buyer = new Buyer();
        buyer.setId(request.getUsername());
        buyer.setName(request.getFirstName());
        buyer.setSurname(request.getLastName());
        buyer.setEmail(request.getEmail());
        buyer.setGsmNumber(request.getPhone());
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress(request.getAddress() != null ? request.getAddress() : request.getStreetAddress());
        buyer.setCity(request.getCity() != null ? request.getCity() : "Istanbul");
        buyer.setCountry(request.getCountry() != null ? request.getCountry() : "Turkey");
        iyzicoRequest.setBuyer(buyer);


        Address billingAddress = new Address();
        billingAddress.setContactName(request.getFirstName() + " " + request.getLastName());
        billingAddress.setCity(buyer.getCity());
        billingAddress.setCountry(buyer.getCountry());
        billingAddress.setAddress(buyer.getRegistrationAddress());
        iyzicoRequest.setBillingAddress(billingAddress);
        iyzicoRequest.setShippingAddress(billingAddress);


        List<BasketItem> basketItems = new ArrayList<>();
        request.getItems().forEach(item -> {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(item.getProductId().toString());
            basketItem.setName(item.getProductName());
            basketItem.setCategory1("General");
//            basketItem.setCategory1(item.getCategory1() != null ? item.getCategory1() : item.getCategory2());
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            basketItem.setPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            basketItems.add(basketItem);
        });
        iyzicoRequest.setBasketItems(basketItems);


        try {
            LOGGER.debug("API CALL: Sending request to Iyzico for Order ID: {}", request.getOrderId());

            Payment payment = Payment.create(iyzicoRequest, options);

            PaymentResponseDto returnPaymentResponseDto = new PaymentResponseDto();
            boolean isSuccess = "success".equalsIgnoreCase(payment.getStatus());

            returnPaymentResponseDto.setSuccess("success".equalsIgnoreCase(payment.getStatus()));
            returnPaymentResponseDto.setMessage(payment.getErrorMessage());
            returnPaymentResponseDto.setTransactionId(payment.getPaymentId());

            if (isSuccess) {
                LOGGER.info("PAYMENT SUCCESS: Transaction approved for Order ID: {}. Payment ID: {}",
                        request.getOrderId(), payment.getPaymentId());
            } else {
                LOGGER.warn("PAYMENT REJECTED: Order ID: {} failed. Reason: {}, Error Code: {}",
                        request.getOrderId(), payment.getErrorMessage(), payment.getErrorCode());
            }

            return returnPaymentResponseDto;

        } catch (Exception e) {
            LOGGER.error("PAYMENT SYSTEM ERROR: Fatal error during Iyzico communication for Order ID: {}",
                    request.getOrderId(), e);

            PaymentResponseDto errorResponse = new PaymentResponseDto();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("System error during payment processing: " + e.getMessage());
            return errorResponse;
        }

    }
}
