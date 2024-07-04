package com.nhh203.promotionservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountCodeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private String code; // Mã giảm giá
	private Boolean isActive;
	private ZonedDateTime startDate;
	private ZonedDateTime endDate;
	private double discountValue; // Giá trị ưu đãi
	private Long idUser;

	@OneToMany(mappedBy = "discountCode", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<DiscountAppEntity> discountAppEntities;
}
