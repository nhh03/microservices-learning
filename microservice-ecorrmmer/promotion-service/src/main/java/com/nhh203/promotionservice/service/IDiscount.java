package com.nhh203.promotionservice.service;

import com.nhh203.promotionservice.model.DiscountAppEntity;
import com.nhh203.promotionservice.model.DiscountCodeEntity;
import com.nhh203.promotionservice.model.dto.PromotionDTO;

import java.util.List;

public interface IDiscount {
    List<DiscountAppEntity> addDiscount(PromotionDTO promotionDTO);

    List<DiscountAppEntity> findByProductId(Long idProduct);

    DiscountCodeEntity findById(Long id);

    List<DiscountAppEntity> findByProductIdBetweenDate(Long idProduct);

    public void updateDiscountCodeStatus(Long id, boolean isActive);

    List<DiscountCodeEntity> findDiscountCodesByUserId(Long idUser);

    List<DiscountAppEntity> updateDiscount(PromotionDTO promotionDTO);
}
