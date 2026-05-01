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



    @Transactional
    public StockUpdateResponseDto reserve(StockUpdateRequestDto req) {
        try {
            // önce tüm ürünleri doğrula
            for (StockItemDto it : req.getItems()) {
                Stock ps = stockRepository.findById(it.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + it.getProductId()));

                if (ps.getAvailableQuantity() < it.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for productId=" + it.getProductId());
                }
            }

            // sonra rezerve et
            for (StockItemDto it : req.getItems()) {
                Stock ps = stockRepository.findById(it.getProductId()).orElseThrow();
                ps.reserve(it.getQuantity());
                stockRepository.save(ps);
            }

            return StockUpdateResponseDto.ok("Stock reserved");
        } catch (Exception e) {
            return StockUpdateResponseDto.fail(e.getMessage());
        }
    }


    @Transactional
    public StockUpdateResponseDto release(StockUpdateRequestDto req) {
        try {
            // önce tüm ürünleri doğrula
            for (StockItemDto it : req.getItems()) {
                Stock ps = stockRepository.findById(it.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + it.getProductId()));

                if (ps.getReservedQuantity() < it.getQuantity()) {
                    throw new IllegalStateException("Insufficient reserved stock for productId=" + it.getProductId());
                }
            }

            // sonra rezervasyonu geri bırak
            for (StockItemDto it : req.getItems()) {
                Stock ps = stockRepository.findById(it.getProductId()).orElseThrow();
                ps.release(it.getQuantity());
                stockRepository.save(ps);
            }

            return StockUpdateResponseDto.ok("Stock released");
        } catch (Exception e) {
            return StockUpdateResponseDto.fail(e.getMessage());
        }
    }


    @Transactional
    public StockUpdateResponseDto commit(StockUpdateRequestDto req) {
        try {
            // önce tüm ürünleri doğrula
            for (StockItemDto it : req.getItems()) {
                Stock ps = stockRepository.findById(it.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + it.getProductId()));

                if (ps.getReservedQuantity() < it.getQuantity()) {
                    throw new IllegalStateException("Insufficient reserved stock for productId=" + it.getProductId());
                }
            }

            // sonra commit et
            for (StockItemDto it : req.getItems()) {
                Stock ps = stockRepository.findById(it.getProductId()).orElseThrow();
                ps.commit(it.getQuantity());
                stockRepository.save(ps);
            }

            return StockUpdateResponseDto.ok("Stock committed");
        } catch (Exception e) {
            return StockUpdateResponseDto.fail(e.getMessage());
        }
    }

}
