package com.n11.bootcamp.ecommerce.stock_service.controller;


import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateRequestDto;
import com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto.StockUpdateResponseDto;
import com.n11.bootcamp.ecommerce.stock_service.service.impl.StockServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stock Management", description = "Stock Operations and Saga Pattern Transaction Management APIs")
public class StockController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockController.class);

    private final StockServiceImpl stockServiceImpl;

    public StockController(StockServiceImpl stockServiceImpl) {
        this.stockServiceImpl = stockServiceImpl;
    }

    @Operation(summary = "Reserve stocks", description = "Decreases available quantity and increases reserved quantity. Part of the Saga orchestration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully reserved"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock level")
    })
    @PostMapping("/reserve")
    public ResponseEntity<StockUpdateResponseDto> reserve(@RequestBody StockUpdateRequestDto request) {
        LOGGER.info("API CALL: Reserve stock request");
        return ResponseEntity.ok(stockServiceImpl.reserve(request));
    }

    @Operation(summary = "Release reserved stocks", description = "Decreases reserved quantity and increases available quantity back. Used during payment failures.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully released"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PostMapping("/release")
    public ResponseEntity<StockUpdateResponseDto> release(@RequestBody StockUpdateRequestDto req) {
        LOGGER.warn("API CALL: Release stock (Compensating Transaction)");
        return ResponseEntity.ok(stockServiceImpl.release(req));
    }

    @Operation(summary = "Commit stock reservation", description = "Decreases reserved quantity permanently. Used when payment is successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully committed"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PostMapping("/commit")
    public ResponseEntity<StockUpdateResponseDto> commit(@RequestBody StockUpdateRequestDto req) {
        LOGGER.info("API CALL: Commit stock");
        return ResponseEntity.ok(stockServiceImpl.commit(req));
    }
}
