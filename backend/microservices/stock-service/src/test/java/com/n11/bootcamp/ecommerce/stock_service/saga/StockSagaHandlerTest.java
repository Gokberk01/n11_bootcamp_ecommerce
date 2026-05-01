package com.n11.bootcamp.ecommerce.stock_service.saga;

import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.ItemDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockRejectedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockReserveRequestedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.eventdto.StockReservedEventDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockSagaHandlerTest {

    @Mock
    private StockServiceImpl stockServiceImpl;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private StockSagaHandler stockSagaHandler;

    private StockReserveRequestedEventDto eventDto;
    private final String EXCHANGE = "stock.events.exchange";
    private final String RESERVED_KEY = "order.stock.reserved";
    private final String REJECTED_KEY = "order.stock.rejected";

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(stockSagaHandler, "exchange", EXCHANGE);
        ReflectionTestUtils.setField(stockSagaHandler, "reservedRoutingKey", RESERVED_KEY);
        ReflectionTestUtils.setField(stockSagaHandler, "rejectedRoutingKey", REJECTED_KEY);

        ItemDto item = new ItemDto(1L, 2);
        eventDto = new StockReserveRequestedEventDto();
        eventDto.setOrderId(100L);
        eventDto.setUsername("kassadin");
        eventDto.setItems(List.of(item));
    }

    @Test
    void handleReserveRequested_Success() {

        StockUpdateResponseDto successResponse = StockUpdateResponseDto.ok("Stock reserved");
        when(stockServiceImpl.reserve(any(StockUpdateRequestDto.class))).thenReturn(successResponse);

        stockSagaHandler.handleReserveRequested(eventDto);

        verify(stockServiceImpl, times(1)).reserve(any(StockUpdateRequestDto.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(EXCHANGE),
                eq(RESERVED_KEY),
                any(StockReservedEventDto.class)
        );
        verify(rabbitTemplate, never()).convertAndSend(eq(EXCHANGE), eq(REJECTED_KEY), any(StockRejectedEventDto.class));
    }

    @Test
    void handleReserveRequested_Rejected() {

        StockUpdateResponseDto failResponse = StockUpdateResponseDto.fail("Insufficient stock");
        when(stockServiceImpl.reserve(any(StockUpdateRequestDto.class))).thenReturn(failResponse);

        stockSagaHandler.handleReserveRequested(eventDto);

        verify(stockServiceImpl, times(1)).reserve(any(StockUpdateRequestDto.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(EXCHANGE),
                eq(REJECTED_KEY),
                any(StockRejectedEventDto.class)
        );
        verify(rabbitTemplate, never()).convertAndSend(eq(EXCHANGE), eq(RESERVED_KEY), any(StockReservedEventDto.class));
    }

    @Test
    void handleReserveRequested_Exception() {
        when(stockServiceImpl.reserve(any(StockUpdateRequestDto.class)))
                .thenThrow(new RuntimeException("Database connection lost"));

        stockSagaHandler.handleReserveRequested(eventDto);

        verify(stockServiceImpl, times(1)).reserve(any(StockUpdateRequestDto.class));
        verifyNoInteractions(rabbitTemplate);
    }
}
