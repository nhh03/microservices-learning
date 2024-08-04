package com.nhh203.productservice.viewmodel.productattribute;

import com.nhh203.productservice.model.attribute.ProductAttributeTemplate;

public record ProductAttributeTemplateGetVm(ProductAttributeVm productAttribute,
                                            Integer displayOrder) {
	public static ProductAttributeTemplateGetVm fromModel(ProductAttributeTemplate productAttributeTemplate) {
		return new ProductAttributeTemplateGetVm(
				ProductAttributeVm.fromModel(productAttributeTemplate.getProductAttribute()),
				productAttributeTemplate.getDisplayOrder()
		);
	}

}
