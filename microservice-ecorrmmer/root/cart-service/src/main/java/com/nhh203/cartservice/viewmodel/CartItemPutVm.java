package com.nhh203.cartservice.viewmodel;

import com.nhh203.cartservice.model.entity.CartItemEntity;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public record CartItemPutVm(Long cartItemId, String userId, String productId, Integer quantity, String status) {
	public static CartItemPutVm fromModel(CartItemEntity cartItem, String status) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String customerId = authentication.getName();
		return new CartItemPutVm(
				cartItem.getId(),
				customerId,
				cartItem.getProductId(),
				cartItem.getQuantity(),
				status);
	}
}
