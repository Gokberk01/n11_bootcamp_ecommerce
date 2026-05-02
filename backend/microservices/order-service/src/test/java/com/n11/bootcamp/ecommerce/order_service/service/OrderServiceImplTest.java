package com.n11.bootcamp.ecommerce.order_service.service;


import com.n11.bootcamp.ecommerce.order_service.dto.order.request.OrderRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.order.request.OrderRequestCard;
import com.n11.bootcamp.ecommerce.order_service.dto.order.request.OrderRequestItem;
import com.n11.bootcamp.ecommerce.order_service.dto.order.response.OrderResponse;
import com.n11.bootcamp.ecommerce.order_service.dto.stock.request.StockReserveRequestedEvent;
import com.n11.bootcamp.ecommerce.order_service.entity.Order;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderDetails;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderItem;
import com.n11.bootcamp.ecommerce.order_service.entity.OrderStatus;
import com.n11.bootcamp.ecommerce.order_service.exception.OrderNotFoundException;
import com.n11.bootcamp.ecommerce.order_service.repository.OrderRepository;
import com.n11.bootcamp.ecommerce.order_service.saga.PaymentCardStore;
import com.n11.bootcamp.ecommerce.order_service.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PaymentCardStore paymentCardStore;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Order order;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "stockExchange", "test-exchange");
        ReflectionTestUtils.setField(orderService, "stockReserveRequestedRoutingKey", "test-key");


        orderRequest = new OrderRequest();
        orderRequest.setUsername("testuser");
        orderRequest.setFirstName("Gokberk");
        orderRequest.setLastName("Ozkan");

        OrderRequestItem itemRequest = new OrderRequestItem();
        itemRequest.setProductId(1L);
        itemRequest.setProductName("Laptop");
        itemRequest.setPrice(100.0);
        itemRequest.setQuantity(2);
        orderRequest.setItems(List.of(itemRequest));

        OrderRequestCard cardRequest = new OrderRequestCard();
        cardRequest.setCardHolderName("Gokberk Ozkan");
        cardRequest.setCardNumber("123456789");
        cardRequest.setExpireMonth("12");
        cardRequest.setExpireYear("2025");
        cardRequest.setCvc("123");
        orderRequest.setCard(cardRequest);


        order = new Order();
        order.setId(1L);
        order.setUsername("testuser");
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(200.0);

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(1L);
        orderItem.setProductName("Laptop");
        orderItem.setPrice(100.0);
        orderItem.setQuantity(2);
        orderItem.setOrder(order);
        order.setItems(List.of(orderItem));

        order.setOrderDetails(new OrderDetails());
    }

    @Test
    void createOrder_WithCard_Success() {

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals(order.getId(), response.getOrderId());
        verify(paymentCardStore, times(1)).put(eq(1L), any());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("test-exchange"), eq("test-key"), any(StockReserveRequestedEvent.class));
    }

    @Test
    void createOrder_WithoutCard_Success() {

        orderRequest.setCard(null);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        verify(paymentCardStore, never()).put(any(), any());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(StockReserveRequestedEvent.class));
    }

    @Test
    void findAllOrders_Success() {

        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderResponse> result = orderService.findAllOrders();

        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getOrderId());
    }

    @Test
    void getOrderById_Found() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
    }

    @Test
    void getOrderById_NotFound() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void findOrdersByUsername_Success() {

        when(orderRepository.findByUsername("testuser")).thenReturn(List.of(order));

        List<OrderResponse> result = orderService.findOrdersByUsername("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
}
