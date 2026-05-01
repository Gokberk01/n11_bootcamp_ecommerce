package com.n11.bootcamp.ecommerce.stock_service.controller;


import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockItemDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    @Mock
    private StockServiceImpl stockServiceImpl;

    @InjectMocks
    private StockController stockController;

    private StockUpdateRequestDto requestDto;
    private StockUpdateResponseDto successResponse;

    @BeforeEach
    void setUp() {
        StockItemDto item = new StockItemDto(1L, 5);
        requestDto = new StockUpdateRequestDto(List.of(item));
        successResponse = StockUpdateResponseDto.ok("Success");
    }

    @Test
    void reserve_Success() {

        when(stockServiceImpl.reserve(any(StockUpdateRequestDto.class))).thenReturn(successResponse);

        ResponseEntity<StockUpdateResponseDto> response = stockController.reserve(requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Success", response.getBody().getMessage());
        verify(stockServiceImpl, times(1)).reserve(requestDto);
    }

    @Test
    void release_Success() {

        when(stockServiceImpl.release(any(StockUpdateRequestDto.class))).thenReturn(successResponse);

        ResponseEntity<StockUpdateResponseDto> response = stockController.release(requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(stockServiceImpl, times(1)).release(requestDto);
    }

    @Test
    void commit_Success() {

        when(stockServiceImpl.commit(any(StockUpdateRequestDto.class))).thenReturn(successResponse);

        ResponseEntity<StockUpdateResponseDto> response = stockController.commit(requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(stockServiceImpl, times(1)).commit(requestDto);
    }

    @Test
    void reserve_Failure() {

        StockUpdateResponseDto failResponse = StockUpdateResponseDto.fail("Insufficient stock");
        when(stockServiceImpl.reserve(any(StockUpdateRequestDto.class))).thenReturn(failResponse);

        ResponseEntity<StockUpdateResponseDto> response = stockController.reserve(requestDto);

        assertEquals(200, response.getStatusCode().value()); // ResponseEntity.ok kullanıldığı için status 200 döner, body içinde fail olur
        assertEquals("Insufficient stock", response.getBody().getMessage());
        assertEquals(false, response.getBody().isSuccess());
    }
}
