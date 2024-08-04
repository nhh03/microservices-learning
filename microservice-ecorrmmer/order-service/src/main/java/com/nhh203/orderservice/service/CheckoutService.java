package com.nhh203.orderservice.service;


import com.nhh203.orderservice.viewmodel.checkout.CheckoutPostVm;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutStatusPutVm;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutVm;

public interface CheckoutService {
	CheckoutVm createCheckout(CheckoutPostVm checkoutPostVm);
	CheckoutVm getCheckoutPendingStateWithItemsById(String id);
	Long updateCheckoutStatus(CheckoutStatusPutVm checkoutStatusPutVm);
}
