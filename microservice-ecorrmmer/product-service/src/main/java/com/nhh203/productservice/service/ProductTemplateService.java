package com.nhh203.productservice.service;

import com.nhh203.productservice.viewmodel.producttemplate.ProductTemplateListGetVm;
import com.nhh203.productservice.viewmodel.producttemplate.ProductTemplatePostVm;
import com.nhh203.productservice.viewmodel.producttemplate.ProductTemplateVm;

public interface ProductTemplateService {
	ProductTemplateListGetVm getPageableProductTemplate(int pageNo, int pageSize);

	ProductTemplateVm getProductTemplate(Long id);

	ProductTemplateVm saveProductTemplate(ProductTemplatePostVm productTemplatePostVm);

	void updateProductTemplate(long productTemplateId, ProductTemplatePostVm productTemplatePostVm);

}
