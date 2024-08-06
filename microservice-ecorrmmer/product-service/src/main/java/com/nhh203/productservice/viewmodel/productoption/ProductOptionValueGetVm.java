package com.nhh203.productservice.viewmodel.productoption;

import com.nhh203.productservice.model.ProductOption;
import com.nhh203.productservice.model.ProductOptionValue;

public record ProductOptionValueGetVm(Long id, String productId,
                                      ProductOption productOption,
                                      String displayType,
                                      int displayOrder, String value,
                                      String productOptionName,
                                      String productOptionValue) {
	public static ProductOptionValueGetVm fromModel(ProductOptionValue productOptionValue) {
		return new ProductOptionValueGetVm(
				productOptionValue.getId(),
				productOptionValue.getProduct().getProductId(),
				productOptionValue.getProductOption(),
				productOptionValue.getDisplayType(),
				productOptionValue.getDisplayOrder(),
				productOptionValue.getValue(),
				productOptionValue.getProductOption().getName(),
				productOptionValue.getValue()
		);
	}
}
