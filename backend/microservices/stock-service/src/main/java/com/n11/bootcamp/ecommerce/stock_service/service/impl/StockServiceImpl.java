package com.n11.bootcamp.ecommerce.stock_service.service.impl;

import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockItemDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.entity.Stock;
import com.n11.bootcamp.ecommerce.stock_service.exception.InsufficientStockException;
import com.n11.bootcamp.ecommerce.stock_service.exception.StockNotFoundException;
import com.n11.bootcamp.ecommerce.stock_service.repository.StockRepository;
import com.n11.bootcamp.ecommerce.stock_service.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockServiceImpl implements StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockServiceImpl.class);

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    @Transactional
    public StockUpdateResponseDto reserve(StockUpdateRequestDto request) {
        LOGGER.info("SERVICE: Reserving stocks for {} items", request.getItems().size());

        for (StockItemDto item : request.getItems()) {
            Stock productStock = stockRepository.findById(item.getProductId())
                    .orElseThrow(() -> {
                        LOGGER.error("SERVICE ERROR: Product not found ID: {}", item.getProductId());
                        return new StockNotFoundException(item.getProductId());
                    });

            if (productStock.getAvailableQuantity() < item.getQuantity()) {
                LOGGER.warn("SERVICE WARN: Insufficient stock for Product ID: {}. Available: {}, Requested: {}",
                        item.getProductId(), productStock.getAvailableQuantity(), item.getQuantity());
                throw new InsufficientStockException("Insufficient stock for productId: " + item.getProductId());
            }
        }

        for (StockItemDto item : request.getItems()) {
            Stock productStock = stockRepository.findById(item.getProductId()).orElseThrow(() -> {
                LOGGER.error("SERVICE ERROR: Product stock not found ID: {}", item.getProductId());
                return new StockNotFoundException(item.getProductId());
                });
            productStock.reserve(item.getQuantity());
            stockRepository.save(productStock);
        }
        LOGGER.info("SERVICE: Stock reservation completed successfully");
        return StockUpdateResponseDto.ok("Stock reserved");
    }


    @Transactional
    public StockUpdateResponseDto release(StockUpdateRequestDto request) {
        LOGGER.info("SERVICE: Releasing reserved stocks (Compensating Transaction)");

        for (StockItemDto item : request.getItems()) {
            Stock productStock = stockRepository.findById(item.getProductId())
                    .orElseThrow(() -> {
                        LOGGER.error("SERVICE ERROR: Stock not found for release. ID: {}", item.getProductId());
                        return new StockNotFoundException(item.getProductId());
                        });

            if (productStock.getReservedQuantity() < item.getQuantity()) {
                LOGGER.error("SERVICE ERROR: Data inconsistency. Reserved quantity lower than release amount for ID: {}", item.getProductId());
                throw new InsufficientStockException("Insufficient reserved stock for productId: " + item.getProductId());
            }
        }

        for (StockItemDto item : request.getItems()) {
            Stock productStock = stockRepository.findById(item.getProductId()).orElseThrow(() -> {
                LOGGER.error("SERVICE ERROR: Product's stock not found ID: {}", item.getProductId());
                return new StockNotFoundException(item.getProductId());
            });
            productStock.release(item.getQuantity());
            stockRepository.save(productStock);
        }

        LOGGER.info("SERVICE: Stock release completed successfully");
        return StockUpdateResponseDto.ok("Stock released");
    }


    @Transactional
    public StockUpdateResponseDto commit(StockUpdateRequestDto req) {
        LOGGER.info("SERVICE: Committing stocks for finalized order");

        for (StockItemDto item : req.getItems()) {
            Stock productStock = stockRepository.findById(item.getProductId())
                    .orElseThrow(() -> {
                        LOGGER.error("SERVICE ERROR: Stock not found for commit. ID: {}", item.getProductId());
                        return new StockNotFoundException(item.getProductId());
                    });

            if (productStock.getReservedQuantity() < item.getQuantity()) {
                LOGGER.error("SERVICE ERROR: Cannot commit. Reserved quantity lower than requested amount for ID: {}", item.getProductId());
                throw new InsufficientStockException("Insufficient reserved stock for productId: " + item.getProductId());
            }
        }

        for (StockItemDto item : req.getItems()) {
            Stock productStock = stockRepository.findById(item.getProductId()).orElseThrow(() -> {
                LOGGER.error("SERVICE ERROR: Product stock not found for commit. ID: {}", item.getProductId());
                return new StockNotFoundException(item.getProductId());
            });;
            productStock.commit(item.getQuantity());
            stockRepository.save(productStock);
        }

        LOGGER.info("SERVICE: Stock commit completed successfully");
        return StockUpdateResponseDto.ok("Stock committed");
    }

}
