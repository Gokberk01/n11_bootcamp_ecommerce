package com.n11.bootcamp.ecommerce.stock_service.controller;


import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.service.impl.StockServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockServiceImpl stock;

    public StockController(StockServiceImpl stock) {
        this.stock = stock;
    }


    // Yeni saga yapısı: availableQuantity düşer, reservedQuantity artar
    @PostMapping("/reserve")
    public ResponseEntity<StockUpdateResponseDto> reserve(@RequestBody StockUpdateRequestDto req) {
        return ResponseEntity.ok(stock.reserve(req));
    }

    // Yeni saga yapısı: payment fail/cancel durumunda reservedQuantity düşer, availableQuantity geri artar
    @PostMapping("/release")
    public ResponseEntity<StockUpdateResponseDto> release(@RequestBody StockUpdateRequestDto req) {
        return ResponseEntity.ok(stock.release(req));
    }

    // Yeni saga yapısı: payment success durumunda reservedQuantity düşer, satış kesinleşir
    @PostMapping("/commit")
    public ResponseEntity<StockUpdateResponseDto> commit(@RequestBody StockUpdateRequestDto req) {
        return ResponseEntity.ok(stock.commit(req));
    }
}
