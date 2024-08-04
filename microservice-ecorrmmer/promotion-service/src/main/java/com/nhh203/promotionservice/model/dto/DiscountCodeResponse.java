package com.nhh203.promotionservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nhh203.promotionservice.model.DiscountAppEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCodeResponse {
	private Long id;
	private String name;
	private String description;
	private String code; // Mã giảm giá
	private Boolean isActive;
	private ZonedDateTime startDate;
	private ZonedDateTime endDate;
	private double discountValue; // Giá trị ưu đãi
	private Long idUser;
	private List<DiscountAppEntity> discountAppEntities;
}
