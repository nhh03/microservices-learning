package com.nhh203.productservice.viewmodel.productattribute;

import java.util.List;

public record ProductAttributeGroupListGetVm(
		List<ProductAttributeGroupVm> productAttributeGroupContent,
		int pageNo,
		int pageSize,
		int totalElements,
		int totalPages,
		boolean isLast
) {
}
