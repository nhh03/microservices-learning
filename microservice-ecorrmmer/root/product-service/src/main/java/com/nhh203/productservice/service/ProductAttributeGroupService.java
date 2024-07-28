package com.nhh203.productservice.service;

import com.nhh203.productservice.model.attribute.ProductAttributeGroup;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeGroupListGetVm;

public interface ProductAttributeGroupService {
	ProductAttributeGroupListGetVm getPageableProductAttributeGroups(int pageNo, int pageSize);
	void save(ProductAttributeGroup productAttributeGroup);
}
