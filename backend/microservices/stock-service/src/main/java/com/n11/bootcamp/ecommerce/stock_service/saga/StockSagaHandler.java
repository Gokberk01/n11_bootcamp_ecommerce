package com.n11.bootcamp.ecommerce.stock_service.saga;

import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockRejectedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockReserveRequestedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockReservedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockItemDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.service.impl.StockServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StockSagaHandler {

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
        StockUpdateRequestDto req = new StockUpdateRequestDto(
                event.getItems().stream()
                        .map(i -> new StockItemDto(i.getProductId(), i.getQuantity()))
                        .collect(Collectors.toList())
        );

        StockUpdateResponseDto resp = stockServiceImpl.reserve(req);

        if (resp.isSuccess()) {
            StockReservedEventDto reserved =
                    new StockReservedEventDto(event.getOrderId(), event.getUsername(), "Stock reserved");
            rabbitTemplate.convertAndSend(exchange, reservedRoutingKey, reserved);
        } else {
            StockRejectedEventDto rejected =
                    new StockRejectedEventDto(event.getOrderId(), event.getUsername(), resp.getMessage());
            rabbitTemplate.convertAndSend(exchange, rejectedRoutingKey, rejected);
        }
    }
}
