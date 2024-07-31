package com.nhh203.inventoryservice.repository;

import com.nhh203.inventoryservice.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
}
