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


    @Value("${stock.rabbit.exchange}")
    private String exchange;

    @Value("${stock.rabbit.reservedRoutingKey}")
    private String reservedRoutingKey;

    @Value("${stock.rabbit.rejectedRoutingKey}")
    private String rejectedRoutingKey;

    public StockSagaHandler(StockServiceImpl stockServiceImpl, RabbitTemplate rabbitTemplate) {
        this.stockServiceImpl = stockServiceImpl;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    @RabbitListener(queues = "${stock.rabbit.reserveRequestedQueue}")
    public void handleReserveRequested(StockReserveRequestedEventDto stockReserveRequestedEventDto) {
        StockUpdateRequestDto stockUpdateRequestDto = new StockUpdateRequestDto(
                stockReserveRequestedEventDto.getItems().stream()
                        .map(item -> new StockItemDto(item.getProductId(), item.getQuantity()))
                        .collect(Collectors.toList())
        );

        StockUpdateResponseDto stockUpdateResponseDto = stockServiceImpl.decreaseStock(stockUpdateRequestDto);

        if (stockUpdateResponseDto.isSuccess()) {
            StockReservedEventDto stockReservedEventDto =
                    new StockReservedEventDto(stockReserveRequestedEventDto.getOrderId(), stockReserveRequestedEventDto.getUsername(), "Stock reserved");
            rabbitTemplate.convertAndSend(exchange, reservedRoutingKey, stockReservedEventDto);
        } else {
            StockRejectedEventDto stockRejectedEventDto =
                    new StockRejectedEventDto(stockReserveRequestedEventDto.getOrderId(), stockReserveRequestedEventDto.getUsername(), stockUpdateResponseDto.getMessage());
            rabbitTemplate.convertAndSend(exchange, rejectedRoutingKey, stockRejectedEventDto);
        }
    }
}
