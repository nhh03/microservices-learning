package com.nhh203.orderservice.viewmodel.checkout;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutPostVm(
		String email,
		String note,
		String couponCode,
		@NotNull
		List<CheckoutItemPostVm> checkoutItemPostVms
) {
}
