package com.nhh203.orderservice.controller;


import com.nhh203.orderservice.service.CheckoutService;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutPostVm;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutStatusPutVm;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutVm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CheckoutController {
	private final CheckoutService checkoutService;

	@PostMapping("/storefront/checkouts")
	public ResponseEntity<CheckoutVm> createCheckout(@Valid @RequestBody CheckoutPostVm checkoutPostVm) {
		return ResponseEntity.ok(checkoutService.createCheckout(checkoutPostVm));
	}

	@GetMapping("/storefront/checkouts/{id}")
	public ResponseEntity<CheckoutVm> getOrderWithItemsById(@PathVariable String id) {
		return ResponseEntity.ok(checkoutService.getCheckoutPendingStateWithItemsById(id));
	}

	@PutMapping("/storefront/checkouts/status")
	public ResponseEntity<Long> updateCheckoutStatus(@Valid @RequestBody CheckoutStatusPutVm checkoutStatusPutVm) {
		return ResponseEntity.ok(checkoutService.updateCheckoutStatus(checkoutStatusPutVm));
	}
}
