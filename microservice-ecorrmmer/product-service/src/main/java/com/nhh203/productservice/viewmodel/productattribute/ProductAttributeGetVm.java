package com.nhh203.productservice.viewmodel.productattribute;


import com.nhh203.productservice.model.attribute.ProductAttribute;

public record ProductAttributeGetVm(long id, String name, String productAttributeGroup) {
	public static ProductAttributeGetVm fromModel(ProductAttribute productAttribute) {
		if (productAttribute.getProductAttributeGroup() != null)
			return new ProductAttributeGetVm(productAttribute.getId(), productAttribute.getName(), productAttribute.getProductAttributeGroup().getName());
		else
			return new ProductAttributeGetVm(productAttribute.getId(), productAttribute.getName(), null);
	}
}
