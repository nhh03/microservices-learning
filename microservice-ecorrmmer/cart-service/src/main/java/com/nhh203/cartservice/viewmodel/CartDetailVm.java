package com.nhh203.cartservice.viewmodel;

import com.nhh203.cartservice.model.entity.CartItemEntity;

public record CartDetailVm(Long id, String productId, int quantity) {

	public static CartDetailVm fromModel(CartItemEntity cartItem) {
		return new CartDetailVm(
				cartItem.getId(),
				cartItem.getProductId(),
				cartItem.getQuantity());
	}
}
