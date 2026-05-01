package com.n11.bootcamp.ecommerce.stock_service.service;


import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockItemDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.entity.Stock;
import com.n11.bootcamp.ecommerce.stock_service.exception.InsufficientStockException;
import com.n11.bootcamp.ecommerce.stock_service.exception.StockNotFoundException;
import com.n11.bootcamp.ecommerce.stock_service.repository.StockRepository;
import com.n11.bootcamp.ecommerce.stock_service.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private StockUpdateRequestDto requestDto;
    private Stock testStock;
    private final Long PRODUCT_ID = 1L;

    @BeforeEach
    void setUp() {
        StockItemDto item = new StockItemDto(PRODUCT_ID, 5);
        requestDto = new StockUpdateRequestDto(List.of(item));
        testStock = new Stock(PRODUCT_ID, "Test Product", 10);
    }


    @Test
    void reserve_Success() {
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testStock));

        StockUpdateResponseDto response = stockService.reserve(requestDto);

        assertTrue(response.isSuccess());
        assertEquals(5, testStock.getAvailableQuantity());
        assertEquals(5, testStock.getReservedQuantity());
        verify(stockRepository, times(2)).findById(PRODUCT_ID);
        verify(stockRepository, times(1)).save(testStock);
    }

    @Test
    void reserve_StockNotFound() {
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> stockService.reserve(requestDto));
        verify(stockRepository, never()).save(any());
    }

    @Test
    void reserve_InsufficientStock() {
        testStock.setAvailableQuantity(2);
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testStock));

        assertThrows(InsufficientStockException.class, () -> stockService.reserve(requestDto));
        verify(stockRepository, never()).save(any());
    }

    @Test
    void reserve_SecondLoopNotFound() {
        when(stockRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.of(testStock))
                .thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> stockService.reserve(requestDto));
    }

    @Test
    void release_Success() {
        testStock.setAvailableQuantity(5);
        testStock.setReservedQuantity(5);
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testStock));

        StockUpdateResponseDto response = stockService.release(requestDto);

        assertTrue(response.isSuccess());
        assertEquals(10, testStock.getAvailableQuantity());
        assertEquals(0, testStock.getReservedQuantity());
        verify(stockRepository, times(1)).save(testStock);
    }

    @Test
    void release_InsufficientReservedStock() {
        testStock.setReservedQuantity(2);
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testStock));

        assertThrows(InsufficientStockException.class, () -> stockService.release(requestDto));
    }

    @Test
    void release_NotFound() {
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        assertThrows(StockNotFoundException.class, () -> stockService.release(requestDto));
    }

    @Test
    void release_SecondLoopNotFound() {
        testStock.setReservedQuantity(10);
        when(stockRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.of(testStock))
                .thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> stockService.release(requestDto));
    }

    @Test
    void commit_Success() {
        testStock.setReservedQuantity(5);
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testStock));

        StockUpdateResponseDto response = stockService.commit(requestDto);

        assertTrue(response.isSuccess());
        assertEquals(0, testStock.getReservedQuantity());
        verify(stockRepository, times(1)).save(testStock);
    }

    @Test
    void commit_InsufficientReserved() {
        testStock.setReservedQuantity(2);
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testStock));

        assertThrows(InsufficientStockException.class, () -> stockService.commit(requestDto));
    }

    @Test
    void commit_NotFound() {
        when(stockRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        assertThrows(StockNotFoundException.class, () -> stockService.commit(requestDto));
    }

    @Test
    void commit_SecondLoopNotFound() {
        testStock.setReservedQuantity(10);
        when(stockRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.of(testStock))
                .thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> stockService.commit(requestDto));
    }
}
