package com.nhh203.productservice.service.implement;


import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.exception.wrapper.DuplicatedException;
import com.nhh203.productservice.model.attribute.ProductAttributeGroup;
import com.nhh203.productservice.repository.ProductAttributeGroupRepository;
import com.nhh203.productservice.service.ProductAttributeGroupService;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeGroupListGetVm;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeGroupVm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductAttributeGroupServiceImpl implements ProductAttributeGroupService {
	private final ProductAttributeGroupRepository productAttributeGroupRepository;

	@Override
	public ProductAttributeGroupListGetVm getPageableProductAttributeGroups(int pageNo, int pageSize) {
		List<ProductAttributeGroupVm> productAttributeGroupVms = new ArrayList<>();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<ProductAttributeGroup> productAttributeGroupPage = productAttributeGroupRepository.findAll(pageable);
		List<ProductAttributeGroup> productAttributeGroups = productAttributeGroupPage.getContent();
		for (ProductAttributeGroup productAttributeGroup : productAttributeGroups) {
			productAttributeGroupVms.add(ProductAttributeGroupVm.fromModel(productAttributeGroup));
		}
		return new ProductAttributeGroupListGetVm(
				productAttributeGroupVms,
				productAttributeGroupPage.getNumber(),
				productAttributeGroupPage.getSize(),
				(int) productAttributeGroupPage.getTotalElements(),
				productAttributeGroupPage.getTotalPages(),
				productAttributeGroupPage.isLast()
		);
	}

	@Override
	public void save(ProductAttributeGroup productAttributeGroup) {
		if (checkExistedName(productAttributeGroup.getName(), productAttributeGroup.getId())) {
			throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, productAttributeGroup.getName());
		}
		productAttributeGroupRepository.save(productAttributeGroup);
	}


	private boolean checkExistedName(String name, Long id) {
		return productAttributeGroupRepository.findExistedName(name, id) != null;
	}
}
