package com.nhh203.cartservice.service;


import com.nhh203.cartservice.viewmodel.ProductThumbnailVm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.nhh203.cartservice.config.ServiceUrlConfig;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final RestClient restClient;
	private final ServiceUrlConfig serviceUrlConfig;

	public List<ProductThumbnailVm> getProducts(List<String> ids) {
		final URI uri = UriComponentsBuilder
				.fromHttpUrl(serviceUrlConfig.product())
				.path("/storefront/products/list-featured")
				.queryParam("productId", ids)
				.build()
				.toUri();

		return restClient.get()
				.uri(uri)
				.retrieve()
				.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {})
				.getBody();
	}
}
