package com.n11.bootcamp.ecommerce.stock_service.saga;

import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockRejectedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockReserveRequestedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockReservedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockItemDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.service.impl.StockServiceImpl;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StockSagaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockSagaHandler.class);

    private final StockServiceImpl stockServiceImpl;
    private final RabbitTemplate rabbitTemplate;


    @Value("${stock.rabbit.exchange}") // stock.events.exchange
    private String exchange;

    @Value("${stock.rabbit.reservedRoutingKey}") // order.stock.reserved
    private String reservedRoutingKey;

    @Value("${stock.rabbit.rejectedRoutingKey}") // order.stock.rejected
    private String rejectedRoutingKey;

    public StockSagaHandler(StockServiceImpl stockServiceImpl, RabbitTemplate rabbitTemplate) {
        this.stockServiceImpl = stockServiceImpl;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Transactional
    @RabbitListener(queues = "${stock.rabbit.reserveRequestedQueue}") // stock.reserve.requested.queue
    public void handleReserveRequested(StockReserveRequestedEventDto event) {
        LOGGER.info("SAGA EVENT RECEIVED: Stock reserve request for Order ID: {} by user: {}",
                event.getOrderId(), event.getUsername());

        StockUpdateRequestDto request = new StockUpdateRequestDto(
                event.getItems().stream()
                        .map(i -> new StockItemDto(i.getProductId(), i.getQuantity()))
                        .collect(Collectors.toList())
        );

        try {
            StockUpdateResponseDto response = stockServiceImpl.reserve(request);

            if (response.isSuccess()) {
                LOGGER.info("SAGA SUCCESS: Stock reserved for Order ID: {}. Sending success event to exchange: {}",
                        event.getOrderId(), exchange);

                StockReservedEventDto reserved =
                        new StockReservedEventDto(event.getOrderId(), event.getUsername(), "Stock reserved");

                rabbitTemplate.convertAndSend(exchange, reservedRoutingKey, reserved);
            } else {
                LOGGER.warn("SAGA REJECTED: Stock reservation failed for Order ID: {}. Reason: {}",
                        event.getOrderId(), response.getMessage());

                StockRejectedEventDto rejected =
                        new StockRejectedEventDto(event.getOrderId(), event.getUsername(), response.getMessage());

                rabbitTemplate.convertAndSend(exchange, rejectedRoutingKey, rejected);
            }
        } catch (Exception e) {
            LOGGER.error("SAGA FATAL ERROR: Unexpected error processing reserve request for Order ID: {}",
                    event.getOrderId(), e);
        }
    }
}
