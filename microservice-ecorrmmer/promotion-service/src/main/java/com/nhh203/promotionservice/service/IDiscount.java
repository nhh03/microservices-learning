package com.nhh203.promotionservice.service;

import com.nhh203.promotionservice.model.DiscountAppEntity;
import com.nhh203.promotionservice.model.DiscountCodeEntity;
import com.nhh203.promotionservice.model.dto.PromotionDTO;

import java.time.ZonedDateTime;
import java.util.List;

public interface IDiscount {
    List<DiscountAppEntity> addDiscount(PromotionDTO promotionDTO);

    List<DiscountAppEntity> findByProductId(String idProduct);

    DiscountCodeEntity findById(Long id);

    List<DiscountAppEntity> findByProductIdBetweenDate(String idProduct);

    public void updateDiscountCodeStatus(Long id, boolean isActive);

    List<DiscountCodeEntity> findDiscountCodesByUserId(Long idUser);

    List<DiscountAppEntity> updateDiscount(PromotionDTO promotionDTO);

    List<DiscountCodeEntity> getActiveDiscountCodesBetweenDates(ZonedDateTime startDate, ZonedDateTime endDate);
     boolean deleteByIdProductIn(List<String> idProducts);
}
