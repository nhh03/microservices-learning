package com.nhh203.cartservice.viewmodel;

import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import jakarta.validation.constraints.Min;

public record CartItemVm(String productId, @Min(1) int quantity, String parentProductId) {
	public static CartItemVm fromModel(CartItemEntity cartItem) {
		return new CartItemVm(cartItem.getProductId(), cartItem.getQuantity(), cartItem.getParentProductId());
	}
}
