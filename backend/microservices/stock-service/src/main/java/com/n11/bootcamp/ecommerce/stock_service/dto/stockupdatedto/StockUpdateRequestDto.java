package com.n11.bootcamp.ecommerce.stock_service.dto.stockupdatedto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class StockUpdateRequestDto {


    @NotNull
    private List<StockItemDto> items;

    public StockUpdateRequestDto() { }

    public StockUpdateRequestDto(List<StockItemDto> items) {
        this.items = items;
    }

    public List<StockItemDto> getItems() { return items; }
    public void setItems(List<StockItemDto> items) { this.items = items; }

}
