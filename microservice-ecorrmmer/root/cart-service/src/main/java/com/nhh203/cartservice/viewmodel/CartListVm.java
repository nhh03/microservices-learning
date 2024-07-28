package com.nhh203.cartservice.viewmodel;

import com.nhh203.cartservice.model.entity.CartEntity;

public record CartListVm(Long id, String customerId) {
	public static CartListVm fromModel(CartEntity cart) {
		return new CartListVm(cart.getId(), cart.getCustomerId());
	}
}
