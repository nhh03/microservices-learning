package com.nhh203.cartservice.viewmodel;

import com.nhh203.cartservice.model.entity.CartEntity;

import java.util.List;
import java.util.stream.Collectors;

public record CartGetDetailVm(Long id, String customerId, Long orderId, List<CartDetailVm> cartDetails) {
	public static CartGetDetailVm fromModel(CartEntity cart) {
		return new CartGetDetailVm(
				cart.getId(),
				cart.getCustomerId(),
				cart.getOrderId(),
				cart.getCartItems().stream().map(CartDetailVm::fromModel).collect(Collectors.toList()));
	}
}
