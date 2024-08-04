package com.nhh203.orderservice.service.impl;

import com.nhh203.orderservice.exception.Forbidden;
import com.nhh203.orderservice.exception.NotFoundException;
import com.nhh203.orderservice.model.Checkout;
import com.nhh203.orderservice.model.CheckoutItem;
import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.model.enumeration.ECheckoutState;
import com.nhh203.orderservice.repository.CheckoutItemRepository;
import com.nhh203.orderservice.repository.CheckoutRepository;
import com.nhh203.orderservice.service.CheckoutService;
import com.nhh203.orderservice.service.OrderService;
import com.nhh203.orderservice.utils.AuthenticationUtils;
import com.nhh203.orderservice.utils.Constants;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutPostVm;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutStatusPutVm;
import com.nhh203.orderservice.viewmodel.checkout.CheckoutVm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nhh203.orderservice.utils.Constants.ERROR_CODE.CHECKOUT_NOT_FOUND;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
	private final CheckoutRepository checkoutRepository;
	private final CheckoutItemRepository checkoutItemRepository;
	private final OrderService orderService;

	@Override
	public CheckoutVm createCheckout(CheckoutPostVm checkoutPostVm) {
		UUID uuid = UUID.randomUUID();
		Checkout checkout = Checkout.builder()
				.id(uuid.toString())
				.email(checkoutPostVm.email())
				.note(checkoutPostVm.note())
				.couponCode(checkoutPostVm.couponCode())
				.checkoutState(ECheckoutState.PENDING)
				.build();
		checkoutRepository.save(checkout);

		Set<CheckoutItem> checkoutItems = checkoutPostVm.checkoutItemPostVms()
				.stream()
				.map(item -> CheckoutItem.builder()
						.productId(item.productId())
						.productName(item.productName())
						.quantity(item.quantity())
						.productPrice(item.productPrice())
						.note(item.note())
						.discountAmount(item.discountAmount())
						.taxPercent(item.taxPercent())
						.taxAmount(item.taxAmount())
						.checkoutId(checkout)
						.build())
				.collect(Collectors.toSet());
		checkoutItemRepository.saveAll(checkoutItems);
		checkout.setCheckoutItem(checkoutItems);
		return CheckoutVm.fromModel(checkout);
	}

	@Override
	public CheckoutVm getCheckoutPendingStateWithItemsById(String id) {
		Checkout checkout = checkoutRepository.findByIdAndCheckoutState(id, ECheckoutState.PENDING)
				.orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, id));
		if (!checkout.getCreatedBy().equals(AuthenticationUtils.getCurrentUserId())) {
			throw new Forbidden(Constants.ERROR_CODE.FORBIDDEN);
		}
		return CheckoutVm.fromModel(checkout);
	}

	@Override
	public Long updateCheckoutStatus(CheckoutStatusPutVm checkoutStatusPutVm) {
		Checkout checkout = checkoutRepository.findById(checkoutStatusPutVm.checkoutId())
				.orElseThrow(() -> new NotFoundException(CHECKOUT_NOT_FOUND, checkoutStatusPutVm.checkoutId()));
		checkout.setCheckoutState(ECheckoutState.valueOf(checkoutStatusPutVm.checkoutStatus()));
		checkoutRepository.save(checkout);
		Order order = orderService.findOrderByCheckoutId(checkoutStatusPutVm.checkoutId());
		return order.getId();
	}
}
