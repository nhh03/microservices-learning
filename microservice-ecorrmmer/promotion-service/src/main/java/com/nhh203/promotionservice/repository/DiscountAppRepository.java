package com.nhh203.promotionservice.repository;

import com.nhh203.promotionservice.model.DiscountAppEntity;
import com.nhh203.promotionservice.model.DiscountCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DiscountAppRepository extends JpaRepository<DiscountAppEntity, Long> {
}
