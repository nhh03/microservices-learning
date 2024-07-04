package com.nhh203.promotionservice.repository;

import com.nhh203.promotionservice.model.DiscountAppEntity;
import com.nhh203.promotionservice.model.DiscountCodeEntity;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;


@Repository
public interface DiscountAppRepository extends JpaRepository<DiscountAppEntity, Long> {


	@Query("SELECT d FROM DiscountAppEntity d WHERE d.idProduct = :idProduct " +
			"AND d.discountCode.startDate <= :currentDate " +
			"AND d.discountCode.endDate >= :currentDate")
	List<DiscountAppEntity> findDiscountAppsByProductIdWithinDateRange(@Param("idProduct") String idProduct,
	                                                                   @Param("currentDate") ZonedDateTime currentDate);

	void deleteByIdProductIn(List<String> idProduct);

	long countByIdProductIn(List<String> idProduct);

}
