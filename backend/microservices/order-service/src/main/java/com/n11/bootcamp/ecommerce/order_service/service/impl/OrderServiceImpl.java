package com.n11.bootcamp.ecommerce.order_service.service.impl;

import com.n11.bootcamp.ecommerce.order_service.dto.order.request.OrderRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.order.response.OrderResponse;
import com.n11.bootcamp.ecommerce.order_service.dto.order.response.OrderResponseItem;
import com.n11.bootcamp.ecommerce.order_service.dto.payment.request.PaymentRequestCard;
import com.n11.bootcamp.ecommerce.order_service.dto.stock.request.StockReserveRequestedEvent;
import com.n11.bootcamp.ecommerce.order_service.dto.stock.request.StockReserveRequestedEventItem;
import com.n11.bootcamp.ecommerce.order_service.entity.Order;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderDetails;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderItem;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderStatus;
import com.n11.bootcamp.ecommerce.order_service.exception.OrderNotFoundException;
import com.n11.bootcamp.ecommerce.order_service.repository.OrderRepository;
import com.n11.bootcamp.ecommerce.order_service.saga.PaymentCardStore;
import com.n11.bootcamp.ecommerce.order_service.service.OrderService;
import com.n11.bootcamp.ecommerce.order_service.service.PaymentServiceClient;
import com.n11.bootcamp.ecommerce.order_service.service.StockServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final StockServiceClient stockServiceClient;
    private final ApplicationEventPublisher publisher;
    private final RabbitTemplate rabbitTemplate;
    private final PaymentCardStore paymentCardStore;


    @Value("${stock.rabbit.exchange}")
    private String stockExchange;


    @Value("${stock.rabbit.reserveRequestedRoutingKey}")
    private String stockReserveRequestedRoutingKey;

    public OrderServiceImpl(OrderRepository orderRepository,
                            PaymentServiceClient paymentServiceClient,
                            StockServiceClient stockServiceClient,
                            ApplicationEventPublisher publisher,
                            RabbitTemplate rabbitTemplate,
                            PaymentCardStore paymentCardStore) {
        this.orderRepository = orderRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.stockServiceClient = stockServiceClient;
        this.publisher = publisher;
        this.rabbitTemplate = rabbitTemplate;
        this.paymentCardStore = paymentCardStore;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        LOGGER.info("ORDER CREATION START: Processing new order for user: {}", request.getUsername());

        Order order = new Order();
        order.setUsername(request.getUsername());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(
                request.getItems().stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum()
        );


        List<OrderItem> items = request.getItems().stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setProductId(dto.getProductId());
            item.setProductName(dto.getProductName());
            item.setPrice(dto.getPrice());
            item.setQuantity(dto.getQuantity());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);


        OrderDetails details = new OrderDetails();
        details.setFirstName(request.getFirstName());
        details.setLastName(request.getLastName());
        details.setStreetAddress(request.getStreetAddress());
        details.setCity(request.getCity());
        details.setCountry(request.getCountry());
        details.setPhone(request.getPhone());
        details.setEmail(request.getEmail());
        order.setOrderDetails(details);


        Order savedOrder = orderRepository.save(order);
        LOGGER.info("Order CREATED. orderId: {}, username: {}, totalPrice: {}",
                savedOrder.getId(), savedOrder.getUsername(), savedOrder.getTotalPrice());


        if (request.getCard() != null) {
            PaymentRequestCard cardForStore = new PaymentRequestCard();
            cardForStore.setCardHolderName(request.getCard().getCardHolderName());
            cardForStore.setCardNumber(request.getCard().getCardNumber());
            cardForStore.setExpireMonth(request.getCard().getExpireMonth());
            cardForStore.setExpireYear(request.getCard().getExpireYear());
            cardForStore.setCvc(request.getCard().getCvc());

            paymentCardStore.put(savedOrder.getId(), cardForStore);
            LOGGER.debug("PAYMENT CARD STORED: Temporarily saved in RAM for Order ID: {}", savedOrder.getId());

        } else {
            LOGGER.warn("PAYMENT CARD MISSING: No card info provided for Order ID: {}.", savedOrder.getId());
        }


        StockReserveRequestedEvent eventPayload = new StockReserveRequestedEvent();
        eventPayload.setOrderId(savedOrder.getId());
        eventPayload.setUsername(savedOrder.getUsername());

        List<StockReserveRequestedEventItem> evItems = new ArrayList<>();
        for (OrderItem it : savedOrder.getItems()) {
            evItems.add(new StockReserveRequestedEventItem(
                    it.getProductId(),
                    it.getQuantity()
            ));
        }
        eventPayload.setItems(evItems);

        LOGGER.info("StockReserveRequestedEvent published. exchange: {}, routingKey: {}, payload: {}",
                stockExchange, stockReserveRequestedRoutingKey, eventPayload);


        rabbitTemplate.convertAndSend(
                stockExchange,                  // stock.events.exchange
                stockReserveRequestedRoutingKey,// order.stock.reserve.requested
                eventPayload
        );


//        OrderResponse response = new OrderResponse();
//        response.setOrderId(savedOrder.getId());
//        response.setUsername(savedOrder.getUsername());
//        response.setStatus(savedOrder.getStatus().name());
//        response.setTotalPrice(savedOrder.getTotalPrice());
//        response.setItems(
//                savedOrder.getItems().stream().map(item -> {
//                    OrderResponseItem i = new OrderResponseItem();
//                    i.setProductId(item.getProductId());
//                    i.setProductName(item.getProductName());
//                    i.setPrice(item.getPrice());
//                    i.setQuantity(item.getQuantity());
//                    return i;
//                }).collect(Collectors.toList())
//        );
//
//        return response;

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public List<OrderResponse> findAllOrders() {
        LOGGER.info("SERVICE CALL: Fetching all orders from repository");
//        return orderRepository.findAll().stream().map(order -> {
//            OrderResponse response = new OrderResponse();
//            response.setOrderId(order.getId());
//            response.setUsername(order.getUsername());
//            response.setStatus(order.getStatus().name());
//            response.setTotalPrice(order.getTotalPrice());
//            response.setItems(
//                    order.getItems().stream().map(item -> {
//                        OrderResponseItem i = new OrderResponseItem();
//                        i.setProductId(item.getProductId());
//                        i.setProductName(item.getProductName());
//                        i.setPrice(item.getPrice());
//                        i.setQuantity(item.getQuantity());
//                        return i;
//                    }).collect(Collectors.toList())
//            );
//            return response;
//        }).collect(Collectors.toList());

        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse getOrderById(Long orderId) {
        LOGGER.info("SERVICE CALL: Fetching order details for ID: {}", orderId);

//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException(orderId));
//
//        OrderResponse response = new OrderResponse();
//        response.setOrderId(order.getId());
//        response.setUsername(order.getUsername());
//        response.setStatus(order.getStatus().name());
//        response.setTotalPrice(order.getTotalPrice());
//        response.setItems(
//                order.getItems().stream().map(item -> {
//                    OrderResponseItem i = new OrderResponseItem();
//                    i.setProductId(item.getProductId());
//                    i.setProductName(item.getProductName());
//                    i.setPrice(item.getPrice());
//                    i.setQuantity(item.getQuantity());
//                    return i;
//                }).collect(Collectors.toList())
//        );
//        return response;

        return orderRepository.findById(orderId)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> {
                    LOGGER.error("SERVICE ERROR: Order not found with ID: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });
    }

    @Override
    @Transactional
    public List<OrderResponse> findOrdersByUsername(String username) {
        LOGGER.info("SERVICE CALL: Fetching orders for user: {}", username);

//        List<Order> orders;
//        try {
//            orders = orderRepository.findByUsername(username);
//        } catch (Throwable t) {
//            orders = orderRepository.findAll().stream()
//                    .filter(o -> username != null && username.equals(o.getUsername()))
//                    .collect(Collectors.toList());
//        }
//
//        return orders.stream().map(order -> {
//            OrderResponse response = new OrderResponse();
//            response.setOrderId(order.getId());
//            response.setUsername(order.getUsername());
//            response.setStatus(order.getStatus().name());
//            response.setTotalPrice(order.getTotalPrice());
//            response.setItems(
//                    order.getItems().stream().map(item -> {
//                        OrderResponseItem i = new OrderResponseItem();
//                        i.setProductId(item.getProductId());
//                        i.setProductName(item.getProductName());
//                        i.setPrice(item.getPrice());
//                        i.setQuantity(item.getQuantity());
//                        return i;
//                    }).collect(Collectors.toList())
//            );
//            return response;
//        }).collect(Collectors.toList());

        return orderRepository.findByUsername(username).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setUsername(order.getUsername());
        response.setStatus(order.getStatus().name());
        response.setTotalPrice(order.getTotalPrice());
        response.setItems(order.getItems().stream().map(item -> {
            OrderResponseItem i = new OrderResponseItem();
            i.setProductId(item.getProductId());
            i.setProductName(item.getProductName());
            i.setPrice(item.getPrice());
            i.setQuantity(item.getQuantity());
            return i;
        }).collect(Collectors.toList()));
        return response;
    }
}
