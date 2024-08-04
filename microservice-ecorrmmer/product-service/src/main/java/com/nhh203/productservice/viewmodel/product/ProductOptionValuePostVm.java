package com.nhh203.productservice.viewmodel.product;

import java.util.List;

public record ProductOptionValuePostVm(
		Long productOptionId,
		String displayType,
		Integer displayOrder,
		List<String> value) {
}
