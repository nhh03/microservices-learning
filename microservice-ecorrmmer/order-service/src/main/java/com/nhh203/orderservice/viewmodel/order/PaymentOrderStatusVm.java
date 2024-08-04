package com.nhh203.orderservice.viewmodel.order;


import lombok.Builder;

@Builder
public record PaymentOrderStatusVm(
		Long orderId,
		String orderStatus,
		Long paymentId,
		String paymentStatus) {
}
