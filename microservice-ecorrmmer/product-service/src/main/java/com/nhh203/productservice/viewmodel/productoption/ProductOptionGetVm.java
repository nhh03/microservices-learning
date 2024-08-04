package com.nhh203.productservice.viewmodel.productoption;

import com.nhh203.productservice.model.ProductOption;

public record ProductOptionGetVm(Long id, String name) {
	public static ProductOptionGetVm fromModel(ProductOption productOption) {
		return new ProductOptionGetVm(productOption.getId(), productOption.getName());
	}
}
