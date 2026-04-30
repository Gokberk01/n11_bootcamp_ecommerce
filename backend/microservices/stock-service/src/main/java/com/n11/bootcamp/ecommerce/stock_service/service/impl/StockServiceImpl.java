package com.n11.bootcamp.ecommerce.stock_service.service.impl;

import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockItemDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.entity.Stock;
import com.n11.bootcamp.ecommerce.stock_service.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockServiceImpl {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    public StockUpdateResponseDto decreaseStock(StockUpdateRequestDto stockUpdateRequestDto) {
        try {

            for (StockItemDto stockItem : stockUpdateRequestDto.getItems()) {
                Stock stock = stockRepository.findById(stockItem.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + stockItem.getProductId()));
                if (stock.getProductQuantity() < stockItem.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for productId=" + stockItem.getProductId());
                }
            }

            for (StockItemDto stockItem : stockUpdateRequestDto.getItems()) {
                Stock stock = stockRepository.findById(stockItem.getProductId()).orElseThrow();
                stock.decreaseProductQuantity(stockItem.getQuantity());
                stockRepository.save(stock);
            }
            return StockUpdateResponseDto.ok("Stock decreased");
        } catch (Exception e) {
            return StockUpdateResponseDto.fail(e.getMessage());
        }
    }


    public StockUpdateResponseDto increaseStock(StockUpdateRequestDto stockUpdateRequestDto) {
        try {
            for (StockItemDto stockItem : stockUpdateRequestDto.getItems()) {
                Stock stock = stockRepository.findById(stockItem.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + stockItem.getProductId()));
                stock.increaseProductQuantity(stockItem.getQuantity());
                stockRepository.save(stock);
            }
            return StockUpdateResponseDto.ok("Stock increased");
        } catch (Exception e) {
            return StockUpdateResponseDto.fail(e.getMessage());
        }
    }

}
