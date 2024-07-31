package com.nhh203.promotionservice.controller;


import com.nhh203.promotionservice.model.DiscountAppEntity;
import com.nhh203.promotionservice.model.DiscountCodeEntity;
import com.nhh203.promotionservice.model.dto.DiscountCodeResponse;
import com.nhh203.promotionservice.model.dto.PromotionDTO;
import com.nhh203.promotionservice.service.IDiscount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {
	private final IDiscount discountCode;
	private final ModelMapper modelMapper;

	@GetMapping("/user/{userId}")
	public List<DiscountCodeEntity> getDiscountCodesByUserId(@PathVariable("userId") Long userId) {
		return discountCode.findDiscountCodesByUserId(userId);
	}

	@GetMapping("/product/{productId}")
	public List<DiscountAppEntity> getDiscountAppsByProductId(@PathVariable("productId") String productId) {
		return discountCode.findByProductIdBetweenDate(productId);
	}

	@GetMapping("/{id}")
	public ResponseEntity<DiscountCodeResponse> getDiscountAppsById(@PathVariable Long id) {
		DiscountCodeEntity discountCodeEntity = discountCode.findById(id);
		DiscountCodeResponse discountCodeResponse = modelMapper.map(discountCodeEntity, DiscountCodeResponse.class);
		return ResponseEntity.ok(discountCodeResponse);
	}

	@PostMapping("/add")
	public ResponseEntity<?> create(@RequestBody PromotionDTO promotionDTO) {
		List<DiscountAppEntity> createdDiscountAppEntities = discountCode.addDiscount(promotionDTO);
		if (createdDiscountAppEntities == null) {
			return ResponseEntity.badRequest().body("Create Promotion Failed");
		}
		return new ResponseEntity<>(createdDiscountAppEntities, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody PromotionDTO promotionDTO) {
		promotionDTO.setId(id);
		List<DiscountAppEntity> updatedDiscountAppEntities = discountCode.updateDiscount(promotionDTO);
		if (updatedDiscountAppEntities == null) {
			return ResponseEntity.badRequest().body("Update Promotion Failed");
		}
		return new ResponseEntity<>(updatedDiscountAppEntities, HttpStatus.OK);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam boolean isActive) {
		try {
			discountCode.updateDiscountCodeStatus(id, isActive);
			return ResponseEntity.ok("Discount code status updated successfully.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to update discount code status.");
		}
	}


}
