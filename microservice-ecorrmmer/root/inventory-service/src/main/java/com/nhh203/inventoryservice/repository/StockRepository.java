package com.nhh203.inventoryservice.repository;

import com.nhh203.inventoryservice.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long>{
}
