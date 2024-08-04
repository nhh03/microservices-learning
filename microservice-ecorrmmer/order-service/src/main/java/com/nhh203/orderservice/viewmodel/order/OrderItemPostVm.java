package com.nhh203.orderservice.viewmodel.order;

import java.math.BigDecimal;

public record OrderItemPostVm(
		String productId,
        String productName,
        int quantity,
        BigDecimal productPrice,
        String note,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent
) {
}
