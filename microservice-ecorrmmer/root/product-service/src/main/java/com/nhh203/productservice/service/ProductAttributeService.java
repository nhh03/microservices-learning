package com.nhh203.productservice.service;

import com.nhh203.productservice.model.attribute.ProductAttribute;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeListGetVm;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributePostVm;
import org.springframework.stereotype.Service;

public interface ProductAttributeService {
	ProductAttributeListGetVm getPageableProductAttributes(int pageNo, int pageSize) ;
	ProductAttribute save(ProductAttributePostVm productAttributePostVm);
	ProductAttribute update(ProductAttributePostVm productAttributePostVm, Long id);
}
