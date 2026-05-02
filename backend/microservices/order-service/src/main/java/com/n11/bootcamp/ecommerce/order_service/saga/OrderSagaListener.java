package com.n11.bootcamp.ecommerce.order_service.saga;


import com.n11.bootcamp.ecommerce.order_service.dto.payment.request.PaymentRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.payment.request.PaymentRequestCard;
import com.n11.bootcamp.ecommerce.order_service.dto.payment.request.PaymentRequestItem;
import com.n11.bootcamp.ecommerce.order_service.dto.payment.response.PaymentResponse;
import com.n11.bootcamp.ecommerce.order_service.dto.stock.request.StockUpdateRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.stock.request.StockUpdateRequestItem;
import com.n11.bootcamp.ecommerce.order_service.entity.Order;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderDetails;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderItem;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderStatus;
import com.n11.bootcamp.ecommerce.order_service.event.StockRejectedEvent;
import com.n11.bootcamp.ecommerce.order_service.event.StockReservedEvent;
import com.n11.bootcamp.ecommerce.order_service.exception.OrderNotFoundException;
import com.n11.bootcamp.ecommerce.order_service.repository.OrderRepository;
import com.n11.bootcamp.ecommerce.order_service.service.PaymentServiceClient;
import com.n11.bootcamp.ecommerce.order_service.service.StockServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderSagaListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSagaListener.class);
    private static final String PAYMENT_METHOD = "IYZICO";


    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final StockServiceClient stockServiceClient;
    private final PaymentCardStore paymentCardStore;

    public OrderSagaListener(OrderRepository orderRepository,
                             PaymentServiceClient paymentServiceClient,
                             StockServiceClient stockServiceClient,
                             PaymentCardStore paymentCardStore) {
        this.orderRepository = orderRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.stockServiceClient = stockServiceClient;
        this.paymentCardStore = paymentCardStore;
    }



    @Transactional
    @RabbitListener(queues = "${order.rabbit.stockReservedQueue}")
    public void onStockReserved(StockReservedEvent event) {
        LOGGER.info("[SAGA] Getting StockReservedEvent: orderId: {}, username: {}, message: {}",
                event.getOrderId(), event.getUsername(), event.getMessage());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.COMPLETED) {
            LOGGER.warn("[SAGA] Order Status completed or cancelled, status: {}, orderId: {}",
                    order.getStatus(), order.getId());
            return;
        }


        order.setStatus(OrderStatus.STOCK_DEDUCTED);
        orderRepository.save(order);


        OrderDetails details = order.getOrderDetails();
        PaymentRequest pr = new PaymentRequest();
        pr.setOrderId(order.getId());
        pr.setUsername(order.getUsername());
        if (details != null) {
            pr.setFirstName(details.getFirstName());
            pr.setLastName(details.getLastName());
            pr.setStreetAddress(details.getStreetAddress());
            pr.setAddress(details.getStreetAddress());
            pr.setEmail(details.getEmail());
        }
        pr.setAmount(order.getTotalPrice());
        pr.setPaymentMethod(PAYMENT_METHOD);


        PaymentRequestCard storedCard = paymentCardStore.take(order.getId());
        if (storedCard == null) {
            LOGGER.warn("[SAGA] Payment Card not found (RAM storage is empty). orderId: {}", order.getId());
            markOrderCancelledAndCompensateStock(order);
            return;
        }
        pr.setCard(storedCard);


        List<PaymentRequestItem> payItems = new ArrayList<>();
        for (OrderItem it : order.getItems()) {
            PaymentRequestItem pi = new PaymentRequestItem();
            pi.setProductId(it.getProductId());
            pi.setProductName(it.getProductName());
            pi.setPrice(it.getPrice());
            pi.setQuantity(it.getQuantity());
            payItems.add(pi);
        }
        pr.setItems(payItems);

        LOGGER.info("[SAGA] Sending Payment request: orderId: {}, amount: {}",
                order.getId(), order.getTotalPrice());

        PaymentResponse resp;
        try {
            resp = paymentServiceClient.makePayment(pr);
        } catch (Exception ex) {
            LOGGER.error("[SAGA] Payment service calling error, orderId: {}", order.getId(), ex);
            markOrderCancelledAndCompensateStock(order);
            return;
        }

        if (resp == null || !resp.isSuccess()) {
            LOGGER.warn("[SAGA] Payment failed. orderId: {}, message: {}",
                    order.getId(), resp != null ? resp.getMessage() : "null response");
            markOrderCancelledAndCompensateStock(order);
            return;
        }


        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        LOGGER.info("[SAGA] Order COMPLETED: orderId: {}", order.getId());
    }


    @Transactional
    @RabbitListener(queues = "${order.rabbit.stockRejectedQueue}")
    public void onStockRejected(StockRejectedEvent event) {
        LOGGER.info("[SAGA] Getting StockRejectedEvent: orderId: {}, username: {}, message: {}",
                event.getOrderId(), event.getUsername(), event.getMessage());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        LOGGER.info("[SAGA] Order CANCELLED (stock rejected): orderId: {}", order.getId());
    }


    private void markOrderCancelledAndCompensateStock(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        try {
            StockUpdateRequest req = new StockUpdateRequest();
            List<StockUpdateRequestItem> items = new ArrayList<>();
            for (OrderItem it : order.getItems()) {
                StockUpdateRequestItem si = new StockUpdateRequestItem();
                si.setProductId(it.getProductId());
                si.setQuantity(it.getQuantity());
                items.add(si);
            }
            req.setItems(items);

            LOGGER.info("[SAGA] Calling stock release. orderId: {}", order.getId());
            stockServiceClient.releaseStock(req);

        } catch (Exception ex) {
            LOGGER.error("[SAGA] Stock released failed. orderId: {}", order.getId(), ex);
        }
    }

}
