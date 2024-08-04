package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.exception.wrapper.DuplicatedException;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.model.ProductOption;
import com.nhh203.productservice.repository.ProductOptionRepository;
import com.nhh203.productservice.service.ProductOptionService;
import com.nhh203.productservice.viewmodel.productoption.ProductOptionGetVm;
import com.nhh203.productservice.viewmodel.productoption.ProductOptionListGetVm;
import com.nhh203.productservice.viewmodel.productoption.ProductOptionPostVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService {
	private final ProductOptionRepository productOptionRepository;

	@Override
	public ProductOptionListGetVm getPageableProductOptions(int pageNo, int pageSize) {
		List<ProductOptionGetVm> productOptionGetVms = new ArrayList<>();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<ProductOption> productOptionPage = productOptionRepository.findAll(pageable);
		List<ProductOption> productOptions = productOptionPage.getContent();
		for (ProductOption productOption : productOptions) {
			productOptionGetVms.add(ProductOptionGetVm.fromModel(productOption));
		}
		return new ProductOptionListGetVm(
				productOptionGetVms,
				productOptionPage.getNumber(),
				productOptionPage.getSize(),
				(int) productOptionPage.getTotalElements(),
				productOptionPage.getTotalPages(),
				productOptionPage.isLast()
		);
	}

	@Override
	public ProductOption create(ProductOptionPostVm productOptionPostVm) {
		if (checkExistedName(productOptionPostVm.name(), null)) {
			throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, productOptionPostVm.name());
		}
		ProductOption productOption = new ProductOption();
		productOption.setName(productOptionPostVm.name());
		return productOptionRepository.saveAndFlush(productOption);
	}

	@Override
	public ProductOption update(ProductOptionPostVm productOptionPostVm, Long id) {
		ProductOption productOption = productOptionRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format(Constants.ERROR_CODE.PRODUCT_OPTION_NOT_FOUND, id)));

		if (checkExistedName(productOptionPostVm.name(), id)) {
			throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, productOptionPostVm.name());
		}
		productOption.setName(productOptionPostVm.name());

		return productOptionRepository.saveAndFlush(productOption);
	}

	private boolean checkExistedName(String name, Long id) {
		return productOptionRepository.findExistedName(name, id) != null;
	}
}
