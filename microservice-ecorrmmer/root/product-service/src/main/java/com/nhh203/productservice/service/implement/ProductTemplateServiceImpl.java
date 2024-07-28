package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.exception.wrapper.BadRequestException;
import com.nhh203.productservice.exception.wrapper.DuplicatedException;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.model.attribute.ProductAttribute;
import com.nhh203.productservice.model.attribute.ProductAttributeTemplate;
import com.nhh203.productservice.model.attribute.ProductTemplate;
import com.nhh203.productservice.repository.ProductAttributeRepository;
import com.nhh203.productservice.repository.ProductAttributeTemplateRepository;
import com.nhh203.productservice.repository.ProductTemplateRepository;
import com.nhh203.productservice.service.ProductTemplateService;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeTemplateGetVm;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeTemplatePostVm;
import com.nhh203.productservice.viewmodel.producttemplate.ProductTemplateListGetVm;
import com.nhh203.productservice.viewmodel.producttemplate.ProductTemplatePostVm;
import com.nhh203.productservice.viewmodel.producttemplate.ProductTemplateVm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductTemplateServiceImpl implements ProductTemplateService {

	private final ProductTemplateRepository productTemplateRepository;
	private final ProductAttributeTemplateRepository productAttributeTemplateRepository;
	private final ProductAttributeRepository productAttributeRepository;

	@Override
	public ProductTemplateListGetVm getPageableProductTemplate(int pageNo, int pageSize) {
		return null;
	}

	@Override
	public ProductTemplateVm getProductTemplate(Long id) {
		Optional<ProductTemplate> productTemplate = productTemplateRepository.findById(id);
		if (productTemplate.isEmpty()) {
			throw new NotFoundException(Constants.ERROR_CODE.PRODUCT_TEMPlATE_IS_NOT_FOUND, id);
		}
		List<ProductAttributeTemplate> productAttributeTemplates = productAttributeTemplateRepository
				.findAllByProductTemplateId(id);
		return new ProductTemplateVm(
				id,
				productTemplate.get().getName(),
				productAttributeTemplates.stream().map(ProductAttributeTemplateGetVm::fromModel).toList()
		);
	}

	@Override
	public ProductTemplateVm saveProductTemplate(ProductTemplatePostVm productTemplatePostVm) {
		validateExistedName(productTemplatePostVm.name(), null);
		ProductTemplate productTemplate = new ProductTemplate();
		productTemplate.setName(productTemplatePostVm.name());
		List<ProductAttributeTemplate> productAttributeTemplates = setAttributeTemplates(productTemplatePostVm.ProductAttributeTemplates(), productTemplate);
		ProductTemplate mainSavedProductTemplate = productTemplateRepository.save(productTemplate);
		productAttributeTemplateRepository.saveAllAndFlush(productAttributeTemplates);
		return getProductTemplate(mainSavedProductTemplate.getId());
	}

	@Override
	public void updateProductTemplate(long productTemplateId, ProductTemplatePostVm productTemplatePostVm) {
		ProductTemplate productTemplate = productTemplateRepository.findById(productTemplateId).orElseThrow(()
				-> new NotFoundException(Constants.ERROR_CODE.PRODUCT_TEMPlATE_IS_NOT_FOUND, productTemplateId));
		List<ProductAttributeTemplate> attributeTemplateList = productAttributeTemplateRepository.findAllByProductTemplateId(productTemplateId);
		productAttributeTemplateRepository.deleteAllByIdInBatch(attributeTemplateList.stream().map(ProductAttributeTemplate::getId).toList());
		List<ProductAttributeTemplate> productAttributeTemplates = setAttributeTemplates(productTemplatePostVm.ProductAttributeTemplates(), productTemplate);
		productTemplate.setName(productTemplatePostVm.name());
		productTemplateRepository.save(productTemplate);
		productAttributeTemplateRepository.saveAllAndFlush(productAttributeTemplates);
	}


	private List<ProductAttributeTemplate> setAttributeTemplates(List<ProductAttributeTemplatePostVm> productAttributeTemplates, ProductTemplate productTemplate) {
		List<ProductAttributeTemplate> attributeTemplateList = new ArrayList<>();
		List<Long> idAttributes = new ArrayList<>(productAttributeTemplates.stream().map(ProductAttributeTemplatePostVm::ProductAttributeId).toList());
		if (CollectionUtils.isNotEmpty(idAttributes)) {
			List<Long> attributes = productTemplate
					.getProductAttributeTemplates()
					.stream()
					.map(productAttributeTemplate -> productAttributeTemplate.getProductAttribute().getId()).sorted().toList();
			if (!CollectionUtils.isEqualCollection(attributes, idAttributes.stream().sorted().toList())) {
				List<ProductAttribute> productAttributes = productAttributeRepository.findAllById(idAttributes);
				if (productAttributes.isEmpty()) {
					throw new BadRequestException(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, idAttributes);
				} else if (productAttributes.size() < idAttributes.size()) {
					idAttributes.removeAll(productAttributes.stream().map(ProductAttribute::getId).toList());
					throw new BadRequestException(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, idAttributes);
				}
				Map<Long, ProductAttribute> productAttributeMap = productAttributes.stream()
						.collect(Collectors.toMap(ProductAttribute::getId, Function.identity()));
				for (ProductAttributeTemplatePostVm attributeTemplatePostVm : productAttributeTemplates) {
					attributeTemplateList.add(ProductAttributeTemplate
							.builder()
							.productAttribute(productAttributeMap.get(attributeTemplatePostVm.ProductAttributeId()))
							.productTemplate(productTemplate)
							.displayOrder(attributeTemplatePostVm.displayOrder())
							.build()
					);
				}
			}
		}
		return attributeTemplateList;
	}


	private void validateExistedName(String name, Long id) {
		if (checkExistedName(name, id)) {
			throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, name);
		}
	}

	private boolean checkExistedName(String name, Long id) {
		return productTemplateRepository.findExistedName(name, id) != null;
	}

}
