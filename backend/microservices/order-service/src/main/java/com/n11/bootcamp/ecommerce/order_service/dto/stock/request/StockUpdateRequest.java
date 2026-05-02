package com.n11.bootcamp.ecommerce.order_service.dto.stock.request;

import java.util.List;

public class StockUpdateRequest {

    private List<StockUpdateRequestItem> items;


    public List<StockUpdateRequestItem> getItems() { return items; }
    public void setItems(List<StockUpdateRequestItem> items) { this.items = items; }
}
