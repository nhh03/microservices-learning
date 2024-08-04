package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.exception.wrapper.BadRequestException;
import com.nhh203.productservice.exception.wrapper.DuplicatedException;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.model.attribute.ProductAttribute;
import com.nhh203.productservice.model.attribute.ProductAttributeGroup;
import com.nhh203.productservice.repository.ProductAttributeGroupRepository;
import com.nhh203.productservice.repository.ProductAttributeRepository;
import com.nhh203.productservice.service.ProductAttributeService;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeGetVm;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeListGetVm;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributePostVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements ProductAttributeService {
	private final ProductAttributeRepository productAttributeRepository;
	private final ProductAttributeGroupRepository productAttributeGroupRepository;

	@Override
	public ProductAttributeListGetVm getPageableProductAttributes(int pageNo, int pageSize) {
		List<ProductAttributeGetVm> productAttributeGetVms = new ArrayList<>();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<ProductAttribute> productAttributePage = productAttributeRepository.findAll(pageable);
		List<ProductAttribute> productAttributes = productAttributePage.getContent();
		for (ProductAttribute productAttribute : productAttributes) {
			productAttributeGetVms.add(ProductAttributeGetVm.fromModel(productAttribute));
		}

		return new ProductAttributeListGetVm(
				productAttributeGetVms,
				productAttributePage.getNumber(),
				productAttributePage.getSize(),
				(int) productAttributePage.getTotalElements(),
				productAttributePage.getTotalPages(),
				productAttributePage.isLast()
		);

	}

	@Override
	public ProductAttribute save(ProductAttributePostVm productAttributePostVm) {
		validateExistedName(productAttributePostVm.name(), null);
		ProductAttribute productAttribute = new ProductAttribute();
		productAttribute.setName(productAttributePostVm.name());

		if (productAttributePostVm.productAttributeGroupId() != null) {
			ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
					.findById(productAttributePostVm.productAttributeGroupId())
					.orElseThrow(() -> new BadRequestException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_GROUP_NOT_FOUND,
							productAttributePostVm.productAttributeGroupId())));
			productAttribute.setProductAttributeGroup(productAttributeGroup);
		}

		return this.productAttributeRepository.saveAndFlush(productAttribute);
	}

	@Override
	public ProductAttribute update(ProductAttributePostVm productAttributePostVm, Long id) {
		validateExistedName(productAttributePostVm.name(), id);
		ProductAttribute productAttribute = productAttributeRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, id)));
		productAttribute.setName(productAttributePostVm.name());

		if (productAttributePostVm.productAttributeGroupId() != null) {
			ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
					.findById(productAttributePostVm.productAttributeGroupId())
					.orElseThrow(() -> new BadRequestException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_GROUP_NOT_FOUND,
							productAttributePostVm.productAttributeGroupId())));
			productAttribute.setProductAttributeGroup(productAttributeGroup);
		}
		return productAttributeRepository.saveAndFlush(productAttribute);
	}

	private void validateExistedName(String name, Long id) {
		if (checkExistedName(name, id)) {
			throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, name);
		}
	}

	private boolean checkExistedName(String name, Long id) {
		return productAttributeRepository.findExistedName(name, id) != null;
	}
}
