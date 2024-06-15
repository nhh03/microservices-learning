package com.nhh203.promotionservice.service.implement;

import com.nhh203.promotionservice.model.DiscountAppEntity;
import com.nhh203.promotionservice.model.DiscountCodeEntity;
import com.nhh203.promotionservice.model.dto.PromotionDTO;
import com.nhh203.promotionservice.service.IDiscount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class PromotionImpl implements IDiscount {


    @Override
    public List<DiscountAppEntity> addDiscount(PromotionDTO promotionDTO) {

        return null;
    }

    @Override
    public List<DiscountAppEntity> findByProductId(Long idProduct) {
        return null;
    }

    @Override
    public DiscountCodeEntity findById(Long id) {
        return null;
    }

    @Override
    public List<DiscountAppEntity> findByProductIdBetweenDate(Long idProduct) {
        return null;
    }

    @Override
    public void updateDiscountCodeStatus(Long id, boolean isActive) {

    }

    @Override
    public List<DiscountCodeEntity> findDiscountCodesByUserId(Long idUser) {
        return null;
    }

    @Override
    public List<DiscountAppEntity> updateDiscount(PromotionDTO promotionDTO) {
        return null;
    }
}
