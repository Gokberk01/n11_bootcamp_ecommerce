package com.n11.bootcamp.ecommerce.order_service.service.impl;

import com.n11.bootcamp.ecommerce.order_service.dto.stock.request.StockUpdateRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.stock.response.StockUpdateResponse;
import com.n11.bootcamp.ecommerce.order_service.service.StockServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockServiceClientImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockServiceClientImpl.class);


    private final StockServiceClient stockServiceClient;

    public StockServiceClientImpl(StockServiceClient stockServiceClient)
    {
        this.stockServiceClient = stockServiceClient;
    }

    public StockUpdateResponse reserveStock(StockUpdateRequest request)
    {
        try {
            return stockServiceClient.reserveStock(request);
        } catch (Exception e) {
            LOGGER.error("Stock service call failed: {}", e.getMessage());
            throw new RuntimeException("Stock service failed",e);
        }
    }
}
