package com.nhh203.inventoryservice.service;

import com.nhh203.inventoryservice.config.ServiceUrlConfig;
import com.nhh203.inventoryservice.model.enumeration.FilterExistInWHSelection;
import com.nhh203.inventoryservice.viewmodel.product.ProductInfoVm;
import com.nhh203.inventoryservice.viewmodel.product.ProductQuantityPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final RestClient restClient;
	private final ServiceUrlConfig serviceUrlConfig;

	public ProductInfoVm getProduct(Long id) {
		return null;
	}

	public List<ProductInfoVm> filterProducts(
			String productName,
			String productSku,
			List<Long> productIds,
			FilterExistInWHSelection selection) {
		return null;
	}

	public void updateProductQuantity(List<ProductQuantityPostVm> productQuantityPostVms) {

	}





}
