package com.n11.bootcamp.ecommerce.order_service.dto.stock.response;

import java.util.List;

public class StockUpdateResponse {

    private boolean success;
    private String message;
    private List<StockUpdateResponseItem> results;



    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<StockUpdateResponseItem> getResults() { return results; }
    public void setResults(List<StockUpdateResponseItem> results) { this.results = results; }
}
