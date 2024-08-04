package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.dto.req.CategoryRequest;
import com.nhh203.productservice.dto.res.CategoryResponse;
import com.nhh203.productservice.exception.wrapper.BadRequestException;
import com.nhh203.productservice.exception.wrapper.CategoryNotFoundException;
import com.nhh203.productservice.exception.wrapper.DuplicatedException;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.helper.CategoryMappingHelper;
import com.nhh203.productservice.model.Category;
import com.nhh203.productservice.repository.CategoryRepository;
import com.nhh203.productservice.repository.CategoryRepositoryPagingAndSorting;
import com.nhh203.productservice.service.CategoryService;
import com.nhh203.productservice.viewmodel.category.CategoryGetDetailVm;
import com.nhh203.productservice.viewmodel.category.CategoryGetVm;
import com.nhh203.productservice.viewmodel.category.CategoryPostVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;
	private final CategoryRepositoryPagingAndSorting categoryRepositoryPagingAndSorting;


	@Override
	public Flux<CategoryResponse> findAll() {
		log.info("Category List Service, fetch all category");
		return Flux.fromIterable(this.categoryRepository.findAll())
				.map(category -> modelMapper.map(category, CategoryResponse.class))
				.distinct()
				.onErrorResume(throwable -> {
					log.error("Error while fetching categories: " + throwable.getMessage());
					return Flux.empty();
				});
	}

	@Override
	public Page<CategoryResponse> findAllCategory(int page, int size) {
		log.info("*** CategoryDto List, service; fetch all categories ***");
		Pageable pageable = PageRequest.of(page, size);
		Page<Category> categoryPage = categoryRepository.findAll(pageable);
		List<CategoryResponse> categoryResponseList = categoryPage.getContent()
				.stream()
				.map(CategoryMappingHelper::mapToCategoryResponse)
				.distinct().collect(Collectors.toList());
		return new PageImpl<>(categoryResponseList, pageable, categoryPage.getTotalElements());
	}

	@Override
	public List<CategoryResponse> getAllCategories(Integer pageNo, Integer pageSize, String sortBy) {
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		Page<Category> pagedResult = categoryRepositoryPagingAndSorting.findAllPagedAndSortedCategories(paging);

		if (pagedResult.hasContent()) {
			return pagedResult.getContent()
					.stream()
					.map(CategoryMappingHelper::mapToCategoryResponse)
					.collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public CategoryResponse findById(Integer categoryId) {
		log.info("CategoryDto Service, fetch category by id");
		Optional<Category> category = this.categoryRepository.findById(categoryId);
		if (category.isPresent()) {
			return CategoryMappingHelper.mapToCategoryResponse(category.get());
		} else {
			throw new CategoryNotFoundException(String.format("Category with id[%d] not found", categoryId));
		}
	}

	@Override
	public Mono<CategoryResponse> save(CategoryRequest categoryRequest) {
		log.info("CategoryDto, service; save category");
		return Mono.fromCallable(() -> {
					Category category = modelMapper.map(categoryRequest, Category.class);
					this.categoryRepository.save(category);
					return modelMapper.map(category, CategoryResponse.class);
				})
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorResume(throwable -> {
					log.error("Error occurred while saving category", throwable);
					return Mono.empty();
				});
	}


	@Override
	public CategoryResponse update(Integer categoryId, CategoryRequest categoryRequest) {
		log.info("CategoryDto Service: Updating category with categoryId");
		try {
			Category category = this.categoryRepository.findById(categoryId)
					.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
			BeanUtils.copyProperties(categoryRequest, category, "categoryId");
			this.categoryRepository.save(category);
			return CategoryMappingHelper.mapToCategoryResponse(category);
		} catch (CategoryNotFoundException e) {
			log.error("Error updating category. Category with id [{}] not found.", categoryId);
			throw new CategoryNotFoundException(String.format("Category with id [%d] not found.", categoryId), e);
		} catch (DataIntegrityViolationException e) {
			log.error("Error updating category: Data integrity violation", e);
			throw new CategoryNotFoundException("Error updating category: Data integrity violation", e);
		} catch (Exception e) {
			log.error("Error updating category", e);
			throw new CategoryNotFoundException("Error updating category", e);
		}


	}

	@Override
	public void deleteById(Integer categoryId) {
		log.info("Void Service, delete category by id");
		try {
			categoryRepository.deleteById(categoryId);
		} catch (CategoryNotFoundException e) {
			log.error("Error delete category", e);
			throw new CategoryNotFoundException("Error updating category", e);
		}
	}

	@Override
	public Category create(CategoryPostVm categoryPostVm) {
		validateDuplicateName(categoryPostVm.name(), null);
		Category category = Category.builder()
				.categoryTitle(categoryPostVm.name())
				.slug(categoryPostVm.slug())
				.description(categoryPostVm.description())
				.displayOrder(categoryPostVm.displayOrder())
				.metaDescription(categoryPostVm.metaDescription())
				.metaKeyword(categoryPostVm.metaKeywords())
				.isPublished(categoryPostVm.isPublish())
				.imageId(categoryPostVm.imageId())
				.build();
		if (categoryPostVm.parentId() != null) {
			Category parent = categoryRepository.findById(categoryPostVm.parentId())
					.orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, categoryPostVm.parentId()));
			category.setParent(parent);
		}
		return this.categoryRepository.saveAndFlush(category);
	}

	@Override
	public void update(CategoryPostVm categoryPostVm, Integer id) {
		validateDuplicateName(categoryPostVm.name(), id);
		Category category = this.categoryRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, id));
		category.setCategoryTitle(categoryPostVm.name());
		category.setSlug(categoryPostVm.slug());
		category.setDescription(categoryPostVm.description());
		category.setDisplayOrder(categoryPostVm.displayOrder());
		category.setMetaDescription(categoryPostVm.metaDescription());
		category.setMetaKeyword(categoryPostVm.metaKeywords());
		category.setIsPublished(categoryPostVm.isPublish());
		category.setImageId(categoryPostVm.imageId());
		if (categoryPostVm.parentId() != null) {
			Category parentCategory = categoryRepository
					.findById(categoryPostVm.parentId())
					.orElseThrow(() -> new BadRequestException(Constants.ERROR_CODE.PARENT_CATEGORY_NOT_FOUND,
							categoryPostVm.parentId()));
			category.setParent(parentCategory);
		}
	}

	@Override
	public CategoryGetDetailVm getCategoryById(Integer id) {
		Category category = categoryRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, id));
		Category parentCategory = category.getParent();
		int parentId = 0;
		if (parentCategory != null) {
			parentId = parentCategory.getCategoryId();
		}
		return new CategoryGetDetailVm(
				category.getCategoryId(),
				category.getCategoryTitle(),
				category.getSlug(),
				category.getDescription(),
				parentId,
				category.getMetaKeyword(),
				category.getMetaDescription(),
				category.getDisplayOrder(),
				category.getIsPublished()
		);
	}

	@Override
	public List<CategoryGetVm> getCategories() {
		List<Category> category = categoryRepository.findAll();
		List<CategoryGetVm> categoryGetVms = new ArrayList<>();
		category.forEach(cate -> {
			CategoryGetVm categoryGetVm = CategoryGetVm.fromModel(cate);
			categoryGetVms.add(categoryGetVm);
		});
		return categoryGetVms;
	}

	private boolean checkExistedName(String name, Integer id) {
		return categoryRepository.findExistedName(name, id) != null;
	}

	private void validateDuplicateName(String name, Integer id) {
		if (checkExistedName(name, id)) {
			throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, name);
		}
	}
}
