package com.nhh203.orderservice.viewmodel.checkout;
import java.math.BigDecimal;

public record CheckoutItemPostVm(
        String productId,
        String productName,
        int quantity,
        BigDecimal productPrice,
        String note,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent
)  {
}
