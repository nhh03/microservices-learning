package com.nhh203.orderservice.controller;


import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.event.EventProducer;
import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.model.enumeration.EOrderStatus;
import com.nhh203.orderservice.service.OrderService;
import com.nhh203.orderservice.viewmodel.order.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

	private final OrderService orderService;
	private final EventProducer eventProducer;

	@GetMapping("/backoffice/orders")
	public ResponseEntity<OrderListVm> getOrders(
			@RequestParam(value = "createdFrom", defaultValue = "#{new java.util.Date(1970-01-01)}", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime createdFrom,
			@RequestParam(value = "createdTo", defaultValue = "#{new java.util.Date()}", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime createdTo,
			@RequestParam(value = "email", defaultValue = "", required = false) String email,
			@RequestParam(value = "productName", defaultValue = "", required = false) String productName,
			@RequestParam(value = "orderStatus", defaultValue = "", required = false) List<EOrderStatus> orderStatus,
			@RequestParam(value = "billingPhoneNumber", defaultValue = "", required = false) String billingPhoneNumber,
			@RequestParam(value = "billingCountry", defaultValue = "", required = false) String billingCountry,
			@RequestParam(value = "warehouse", defaultValue = "", required = false) String warehouse,
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
	) {
		return ResponseEntity.ok(orderService.getAllOrder(
				createdFrom,
				createdTo,
				warehouse,
				productName,
				orderStatus,
				billingCountry,
				billingPhoneNumber,
				email,
				pageNo,
				pageSize));
	}

	@GetMapping("/backoffice/orders/{id}")
	public ResponseEntity<OrderVm> getOrderWithItemsById(@PathVariable long id) {
		return ResponseEntity.ok(orderService.getOrderWithItemsById(id));
	}

	@GetMapping("/storefront/orders/my-orders")
	public ResponseEntity<List<OrderGetVm>> getMyOrders(@RequestParam String productName,
	                                                    @RequestParam(required = false) EOrderStatus orderStatus) {
		return ResponseEntity.ok(orderService.getMyOrders(productName, orderStatus));
	}

	@GetMapping("/storefront/orders/completed")
	public ResponseEntity<OrderExistsByProductAndUserGetVm> checkOrderExistsByProductIdAndUserIdWithStatus(
			@RequestParam String productId) {
		return ResponseEntity.ok(orderService.isOrderCompletedWithUserIdAndProductId(productId));
	}


	@Deprecated
	@GetMapping("/user/{phoneNumber}/{status}")
	public ResponseEntity<?> getOrdersByUser(@PathVariable String phoneNumber, @PathVariable String status, @RequestHeader("Authorization") String token) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByUser(phoneNumber, status, token));
	}

	@Deprecated
	@GetMapping("/seller/{sellerId}/{status}")
	public ResponseEntity<?> getOrdersBySeller(@PathVariable String sellerId, @PathVariable String status, @RequestHeader("Authorization") String token) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderBySeller(Long.valueOf(sellerId), status, token));
	}

	@Deprecated
	@GetMapping("/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
		Order order = orderService.getOneOder(orderId);
		if (order != null) {
			return ResponseEntity.ok(order);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Deprecated
	@PostMapping("{statuspayment}")
	public ResponseEntity<?> createOrder(@PathVariable("statuspayment") String statuspayment, @RequestBody OrderRequest orderRequest) {
		Order createdOrder = orderService.createOder(orderRequest);
		if (createdOrder != null) {
			if (statuspayment.equals("0")) {
				String content = createdOrder.getId().toString();
				eventProducer.sendMessage("payment-cod", content);
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order not created");
		}
	}

	@PostMapping("/storefront/orders")
	public ResponseEntity<OrderVm> createOrder(@Valid @RequestBody OrderPostVm orderPostVm) {
		OrderVm orderVm = orderService.createOrder(orderPostVm);
		return ResponseEntity.status(HttpStatus.CREATED).body(orderVm);
	}

	@PutMapping("/storefront/orders/status")
	public ResponseEntity<PaymentOrderStatusVm> updateOrderPaymentStatus(@Valid @RequestBody PaymentOrderStatusVm paymentOrderStatusVm) {
		PaymentOrderStatusVm orderStatusVm = orderService.updateOrderPaymentStatus(paymentOrderStatusVm);
		return ResponseEntity.ok(orderStatusVm);
	}

	@Deprecated
	@PutMapping("/update/{orderId}/status")
	public ResponseEntity<Void> updateStatusOrder(@PathVariable Long orderId, @RequestParam String statusOrder, @RequestParam(defaultValue = "0") String token) {
		if (token.equals("0")) {
			orderService.updateStatusOrder(orderId, statusOrder);
		} else {
			orderService.updateStatusOrder(orderId, statusOrder, token);
		}
		return ResponseEntity.ok().build();
	}

	@Deprecated
	@PutMapping("/update/{orderId}/delivery")
	public ResponseEntity<Void> updateStatusDeliveryOrder(@PathVariable Long orderId, @RequestParam String statusDelivery) {
		orderService.updateStatusDeliveryOrder(orderId, statusDelivery);
		return ResponseEntity.ok().build();
	}


	//	@PostMapping
	//	@ResponseStatus(HttpStatus.CREATED)
	//	//    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
	//	//    @TimeLimiter(name = "inventory")
	//	//    @Retry(name = "inventory")
	//	public String placeOrder(@RequestBody OrderRequest orderRequest) {
	//		log.info("Placing Order");
	//		//        return CompletableFuture.supplyAsync(() -> );
	//		return orderService.placeOrder(orderRequest);
	//	}


	//	public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException exception) {
	//		log.info("Cannot Place Order Executing Fallback logic");
	//		return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time!");
	//
	//	}
}
