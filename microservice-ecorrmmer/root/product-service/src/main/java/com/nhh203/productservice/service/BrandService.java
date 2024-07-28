package com.nhh203.productservice.service;

import com.nhh203.productservice.model.Brand;
import com.nhh203.productservice.viewmodel.brand.BrandListGetVm;
import com.nhh203.productservice.viewmodel.brand.BrandPostVm;


public interface BrandService {
	BrandListGetVm getBrands(int pageNo, int pageSize);
	Brand create(BrandPostVm brandPostVm);
	Brand update(BrandPostVm brandPostVm, Long id);
}
