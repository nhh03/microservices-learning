package com.yas.paymentpaypal.service;

import com.yas.paymentpaypal.config.ServiceUrlConfig;
import com.yas.paymentpaypal.utils.AuthenticationUtils;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

	private final RestClient restClient;
	private final ServiceUrlConfig serviceUrlConfig;

	public void capturePayment(CapturedPaymentVm completedPayment) {
		final URI url = UriComponentsBuilder
				.fromHttpUrl(serviceUrlConfig.payment())
				.path("/storefront/payments/capture")
				.buildAndExpand()
				.toUri();

		restClient.post()
				.uri(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthenticationUtils.getToken())
				.body(completedPayment)
				.retrieve();
	}
}
