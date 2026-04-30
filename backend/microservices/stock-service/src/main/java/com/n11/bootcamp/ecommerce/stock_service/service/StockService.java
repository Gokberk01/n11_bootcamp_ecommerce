package com.n11.bootcamp.ecommerce.stock_service.service;

import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;

public interface StockService {
    public StockUpdateResponseDto decreaseStock(StockUpdateRequestDto stockUpdateRequestDto);
    public StockUpdateResponseDto increaseStock(StockUpdateRequestDto stockUpdateRequestDto);
}
