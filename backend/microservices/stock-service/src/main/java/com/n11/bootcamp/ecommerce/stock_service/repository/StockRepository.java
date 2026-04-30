package com.n11.bootcamp.ecommerce.stock_service.repository;

import com.n11.bootcamp.ecommerce.stock_service.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock,Long> {
}
