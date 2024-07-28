package com.nhh203.productservice.viewmodel.productattribute;

import com.nhh203.productservice.model.attribute.ProductAttributeGroup;

public record ProductAttributeGroupVm(Long id, String name) {
	public static ProductAttributeGroupVm fromModel(ProductAttributeGroup productAttributeGroup) {
		return new ProductAttributeGroupVm(productAttributeGroup.getId(), productAttributeGroup.getName());
	}
}
