package com.n11.bootcamp.ecommerce.order_service.controller;


import com.n11.bootcamp.ecommerce.order_service.dto.order.request.OrderRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.order.response.OrderResponse;
import com.n11.bootcamp.ecommerce.order_service.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderServiceImpl;

    @InjectMocks
    private OrderController orderController;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest();
        orderRequest.setUsername("gokberkozkan");

        orderResponse = new OrderResponse();
        orderResponse.setOrderId(1L);
        orderResponse.setUsername("gokberkozkan");
        orderResponse.setStatus("CREATED");
    }

    @Test
    void createOrder_Success() {

        when(orderServiceImpl.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        OrderResponse result = orderController.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(orderResponse.getOrderId(), result.getOrderId());
        assertEquals(orderResponse.getUsername(), result.getUsername());
        verify(orderServiceImpl, times(1)).createOrder(orderRequest);
    }

    @Test
    void getAllOrders_Success() {

        List<OrderResponse> responseList = Collections.singletonList(orderResponse);
        when(orderServiceImpl.findAllOrders()).thenReturn(responseList);

        List<OrderResponse> result = orderController.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderResponse.getOrderId(), result.get(0).getOrderId());
        verify(orderServiceImpl, times(1)).findAllOrders();
    }

    @Test
    void getOrderById_Success() {

        Long orderId = 1L;
        when(orderServiceImpl.getOrderById(orderId)).thenReturn(orderResponse);

        OrderResponse result = orderController.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        verify(orderServiceImpl, times(1)).getOrderById(orderId);
    }

    @Test
    void getOrdersByUser_Success() {

        String username = "gokberkozkan";
        List<OrderResponse> responseList = Collections.singletonList(orderResponse);
        when(orderServiceImpl.findOrdersByUsername(username)).thenReturn(responseList);

        List<OrderResponse> result = orderController.getOrdersByUser(username);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(username, result.get(0).getUsername());
        verify(orderServiceImpl, times(1)).findOrdersByUsername(username);
    }
}
