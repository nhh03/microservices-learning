package com.nhh203.promotionservice.repository;

import com.nhh203.promotionservice.model.DiscountCodeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;


@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCodeEntity, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE DiscountCodeEntity d SET d.isActive = :status WHERE d.id = :id")
    void updateDiscountCodeStatusById(@Param("id") Long id, @Param("status") boolean status);

    List<DiscountCodeEntity> findByIdUser(Long idUser);

    @Query("SELECT d FROM DiscountCodeEntity d WHERE d.isActive = true AND d.startDate >= :startDate AND d.endDate <= :endDate")
    List<DiscountCodeEntity> findActiveDiscountCodesBetweenDates(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);
}
