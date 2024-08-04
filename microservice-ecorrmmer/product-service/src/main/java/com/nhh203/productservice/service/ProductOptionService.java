package com.nhh203.productservice.service;

import com.nhh203.productservice.model.ProductOption;
import com.nhh203.productservice.viewmodel.productoption.ProductOptionListGetVm;
import com.nhh203.productservice.viewmodel.productoption.ProductOptionPostVm;

public interface ProductOptionService {
	ProductOptionListGetVm getPageableProductOptions(int pageNo, int pageSize);
	ProductOption create(ProductOptionPostVm productOptionPostVm);
	ProductOption update(ProductOptionPostVm productOptionPostVm, Long id);
}
