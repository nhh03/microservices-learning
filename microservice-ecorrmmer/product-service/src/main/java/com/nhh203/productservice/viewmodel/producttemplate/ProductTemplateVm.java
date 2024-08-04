package com.nhh203.productservice.viewmodel.producttemplate;

import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeTemplateGetVm;

import java.util.List;

public record ProductTemplateVm(
		Long id,
		String name,
		List<ProductAttributeTemplateGetVm> productAttributeTemplates) {
}
