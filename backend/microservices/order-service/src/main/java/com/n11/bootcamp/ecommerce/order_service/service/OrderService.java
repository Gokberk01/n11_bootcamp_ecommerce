package com.n11.bootcamp.ecommerce.order_service.service;

import com.n11.bootcamp.ecommerce.order_service.dto.order.request.OrderRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.order.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);
    List<OrderResponse> findAllOrders();
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> findOrdersByUsername(String username);

}
