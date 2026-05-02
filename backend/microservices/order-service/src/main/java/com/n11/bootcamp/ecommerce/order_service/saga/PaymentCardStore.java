package com.n11.bootcamp.ecommerce.order_service.saga;


import com.n11.bootcamp.ecommerce.order_service.dto.payment.request.PaymentRequestCard;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Component
public class PaymentCardStore {

    private final ConcurrentMap<Long, PaymentRequestCard> store = new ConcurrentHashMap<>();

    public void put(Long orderId, PaymentRequestCard card) {
        if (orderId == null || card == null) return;

        PaymentRequestCard copyCard = new PaymentRequestCard();
        copyCard.setCardHolderName(card.getCardHolderName());
        copyCard.setCardNumber(card.getCardNumber());
        copyCard.setExpireMonth(card.getExpireMonth());
        copyCard.setExpireYear(card.getExpireYear());
        copyCard.setCvc(card.getCvc());

        store.put(orderId, copyCard);
    }

    public PaymentRequestCard take(Long orderId) {
        if (orderId == null) return null;
        return store.remove(orderId);
    }
}
