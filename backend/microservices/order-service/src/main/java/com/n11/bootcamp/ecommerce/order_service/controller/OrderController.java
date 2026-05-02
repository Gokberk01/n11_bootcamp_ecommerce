package com.n11.bootcamp.ecommerce.order_service.controller;

import com.n11.bootcamp.ecommerce.order_service.dto.order.request.OrderRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.order.response.OrderResponse;
import com.n11.bootcamp.ecommerce.order_service.service.impl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
@Tag(name = "Order Management", description = "APIs for creating, tracking, and managing customer orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderServiceImpl orderServiceImpl;

    public OrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @Operation(
            summary = "Create a new order",
            description = "Initiates a new order and triggers the Saga orchestration."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order successfully created and processing started"),
            @ApiResponse(responseCode = "400", description = "Invalid order request data")
    })
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        LOGGER.info("API CALL: Create order request received for user: {}", request.getUsername());
        return orderServiceImpl.createOrder(request);
    }

    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders in the system.")
    @GetMapping("/all")
    public List<OrderResponse> getAllOrders() {
        LOGGER.info("API CALL: Fetching all orders");
        return orderServiceImpl.findAllOrders();
    }

    @Operation(summary = "Get order by ID", description = "Retrieves detailed information about a specific order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        LOGGER.info("API CALL: Fetching order with ID: {}", id);
        return orderServiceImpl.getOrderById(id);
    }

    @Operation(summary = "Get orders by username", description = "Retrieves the order history for a specific customer.")
    @GetMapping("/user/{username}")
    public List<OrderResponse> getOrdersByUser(@PathVariable String username) {
        LOGGER.info("API CALL: Fetching orders for username: {}", username);
        return orderServiceImpl.findOrdersByUsername(username);
    }
}