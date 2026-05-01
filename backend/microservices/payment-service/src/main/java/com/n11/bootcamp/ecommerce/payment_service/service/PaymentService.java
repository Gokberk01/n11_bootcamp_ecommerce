package com.n11.bootcamp.ecommerce.payment_service.service;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.n11.bootcamp.ecommerce.payment_service.dto.request.PaymentRequestDto;
import com.n11.bootcamp.ecommerce.payment_service.dto.response.PaymentResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final Options options;

    public PaymentService(Options options)
    {
        this.options = options;
    }

    public PaymentResponseDto processPayment(PaymentRequestDto request) {

        // Iyzico isteği oluştur
        CreatePaymentRequest iyzicoReq = new CreatePaymentRequest();

        iyzicoReq.setPrice(request.getAmount());
        iyzicoReq.setPaidPrice(request.getAmount());
        iyzicoReq.setCurrency(Currency.TRY.name());
        iyzicoReq.setBasketId(request.getOrderId().toString());
        iyzicoReq.setPaymentChannel(PaymentChannel.WEB.name());
        iyzicoReq.setPaymentGroup(PaymentGroup.PRODUCT.name());

        // Kart Bilgileri
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(request.getCard().getCardHolderName());
        paymentCard.setCardNumber(request.getCard().getCardNumber());
        paymentCard.setExpireMonth(request.getCard().getExpireMonth());
        paymentCard.setExpireYear(request.getCard().getExpireYear());
        paymentCard.setCvc(request.getCard().getCvc());
        paymentCard.setRegisterCard(0);
        iyzicoReq.setPaymentCard(paymentCard);

        // Müşteri Bilgileri (Buyer)
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
        iyzicoReq.setBuyer(buyer);


        Address billingAddress = new Address();
        billingAddress.setContactName(request.getFirstName() + " " + request.getLastName());
        billingAddress.setCity(buyer.getCity());
        billingAddress.setCountry(buyer.getCountry());
        billingAddress.setAddress(buyer.getRegistrationAddress());
        iyzicoReq.setBillingAddress(billingAddress);
        iyzicoReq.setShippingAddress(billingAddress);


        // Sepet İçeriği (Basket Items)
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
        iyzicoReq.setBasketItems(basketItems);

        // API Çağrısı
        Payment payment = Payment.create(iyzicoReq, options);

        PaymentResponseDto returnPaymentResponseDto = new PaymentResponseDto();

        returnPaymentResponseDto.setSuccess("success".equalsIgnoreCase(payment.getStatus()));
        returnPaymentResponseDto.setMessage(payment.getErrorMessage());
        returnPaymentResponseDto.setTransactionId(payment.getPaymentId());


        return returnPaymentResponseDto;

    }
}
