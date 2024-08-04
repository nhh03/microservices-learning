package com.nhh203.productservice.viewmodel.product;

import com.nhh203.productservice.model.Product;

public record ProductGetDetailVm(String id, String name, String slug) {
	public static ProductGetDetailVm fromModel(Product product) {
		return new ProductGetDetailVm(product.getProductId(), product.getProductTitle(), product.getSlug());
	}
}