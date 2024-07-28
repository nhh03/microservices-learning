package com.nhh203.productservice.viewmodel.product;

import com.nhh203.productservice.model.Product;

import java.time.Instant;
import java.time.ZonedDateTime;

public record ProductListVm(
		String id,
		String name,
		String slug,
		Boolean isAllowedToOrder,
		Boolean isPublished,
		Boolean isFeatured,
		Boolean isVisibleIndividually,
		String createdOn,
		Long taxClassId
) {
	public static ProductListVm fromModel(Product product) {
		return new ProductListVm(
				product.getProductId(),
				product.getProductTitle(),
				product.getSlug(),
				product.isAllowedToOrder(),
				product.isPublished(),
				product.isFeatured(),
				product.isVisibleIndividually(),
				product.getCreateAt().toString(),
				product.getTaxClassId()
		);
	}
}
