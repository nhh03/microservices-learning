package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.exception.wrapper.DuplicatedException;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.model.Brand;
import com.nhh203.productservice.repository.BrandRepository;
import com.nhh203.productservice.service.BrandService;
import com.nhh203.productservice.viewmodel.brand.BrandListGetVm;
import com.nhh203.productservice.viewmodel.brand.BrandPostVm;
import com.nhh203.productservice.viewmodel.brand.BrandVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.nhh203.productservice.Utils.Constants;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
	private final BrandRepository brandRepository;

	@Override
	public BrandListGetVm getBrands(int pageNo, int pageSize) {
		List<BrandVm> brandVms = new ArrayList<>();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Brand> brandPage = this.brandRepository.findAll(pageable);
		List<Brand> brandList = brandPage.getContent();
		for (Brand brand : brandList) {
			brandVms.add(BrandVm.fromModel(brand));
		}
		return new BrandListGetVm(
				brandVms,
				brandPage.getNumber(),
				brandPage.getSize(),
				(int) brandPage.getTotalElements(),
				brandPage.getTotalPages(),
				brandPage.isLast()
		);
	}

	@Override
	public Brand create(BrandPostVm brandPostVm) {
		validateExistedName(brandPostVm.name(), null);
		return brandRepository.save(brandPostVm.toModel());
	}

	@Override
	public Brand update(BrandPostVm brandPostVm, Long id) {
		validateExistedName(brandPostVm.name(), id);
		Brand brand = brandRepository.findById(id).orElseThrow(
				() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, id)
		);
		brand.setSlug(brandPostVm.slug());
		brand.setName(brand.getName());
		brand.setPublished(brandPostVm.isPublish());
		return null;
	}


	private void validateExistedName(String name, Long id) {
		if (checkExistedName(name, id)) {
			throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, name);
		}

	}

	private boolean checkExistedName(String name, Long id) {
		return brandRepository.findExistedName(name, id) != null;
	}

}
