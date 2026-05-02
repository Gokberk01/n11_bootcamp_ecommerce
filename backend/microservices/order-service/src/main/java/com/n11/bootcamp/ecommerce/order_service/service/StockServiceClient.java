package com.n11.bootcamp.ecommerce.order_service.service;

import com.n11.bootcamp.ecommerce.order_service.dto.stock.request.StockUpdateRequest;
import com.n11.bootcamp.ecommerce.order_service.dto.stock.response.StockUpdateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "stock-service", path = "/api/stocks")
public interface StockServiceClient {

    @PostMapping("/reserve")
    StockUpdateResponse reserveStock(@RequestBody StockUpdateRequest request);

    @PostMapping("/release")
    StockUpdateResponse releaseStock(@RequestBody StockUpdateRequest request);

    @PostMapping("/commit")
    StockUpdateResponse commitStock(@RequestBody StockUpdateRequest request);
}
