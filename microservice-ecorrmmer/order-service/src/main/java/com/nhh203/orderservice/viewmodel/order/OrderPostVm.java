package com.nhh203.orderservice.viewmodel.order;

import com.nhh203.orderservice.model.enumeration.EDeliveryMethod;
import com.nhh203.orderservice.model.enumeration.EPaymentMethod;
import com.nhh203.orderservice.model.enumeration.EPaymentStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderPostVm(
		String checkoutId,
        String email,
        OrderAddressPostVm shippingAddressPostVm,
        OrderAddressPostVm billingAddressPostVm,
        String note,
        float tax,
        float discount,
        int numberItem,
        BigDecimal totalPrice,
        BigDecimal deliveryFee,
        String couponCode,
        EDeliveryMethod deliveryMethod,
        EPaymentMethod paymentMethod,
        EPaymentStatus paymentStatus,
		String phoneNumber,
		Long idSeller,
        @NotNull
		List<OrderItemPostVm> orderItemPostVms) {
}
