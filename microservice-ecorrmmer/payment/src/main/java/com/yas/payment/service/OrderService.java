package com.yas.payment.service;

import com.yas.payment.utils.AuthenticationUtils;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.CheckoutStatusVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import com.yas.payment.config.ServiceUrlConfig;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final RestClient restClient;
	private final ServiceUrlConfig serviceUrlConfig;

	public Long updateCheckoutStatus(CapturedPayment capturedPayment) {
		final URI url = UriComponentsBuilder
				.fromHttpUrl(serviceUrlConfig.order())
				.path("/storefront/checkouts/status")
				.buildAndExpand()
				.toUri();
		CheckoutStatusVm checkoutStatusVm = new CheckoutStatusVm(capturedPayment.checkoutId(),
				capturedPayment.paymentStatus().name());

		return restClient.put()
				.uri(url)
				.header("Authorization", "Bearer " + AuthenticationUtils.getToken())
				.body(checkoutStatusVm).retrieve().body(Long.class);
	}

	public PaymentOrderStatusVm updateOrderStatus(PaymentOrderStatusVm orderPaymentStatusVm) {

		final URI url = UriComponentsBuilder
				.fromHttpUrl(serviceUrlConfig.order())
				.path("/storefront/orders/status")
				.buildAndExpand()
				.toUri();

		return restClient.put()
				.uri(url)
				.header("Authorization", "Bearer " + AuthenticationUtils.getToken())
				.body(orderPaymentStatusVm)
				.retrieve()
				.body(PaymentOrderStatusVm.class);
	}
}
