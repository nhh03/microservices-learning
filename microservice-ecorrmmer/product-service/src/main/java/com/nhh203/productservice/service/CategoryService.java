package com.nhh203.productservice.service;

import com.nhh203.productservice.dto.req.CategoryRequest;
import com.nhh203.productservice.dto.res.CategoryResponse;
import com.nhh203.productservice.model.Category;
import com.nhh203.productservice.viewmodel.category.CategoryGetDetailVm;
import com.nhh203.productservice.viewmodel.category.CategoryGetVm;
import com.nhh203.productservice.viewmodel.category.CategoryPostVm;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CategoryService {

	Flux<CategoryResponse> findAll();

	Page<CategoryResponse> findAllCategory(int page, int size);

	List<CategoryResponse> getAllCategories(Integer pageNo, Integer pageSize, String sortBy);

	CategoryResponse findById(final Integer categoryId);

	Mono<CategoryResponse> save(final CategoryRequest categoryRequest);

	CategoryResponse update(final Integer categoryId, final CategoryRequest categoryDto);

	void deleteById(final Integer categoryId);

	Category create(CategoryPostVm categoryPostVm);

	void update(CategoryPostVm categoryPostVm, Integer id);

	public CategoryGetDetailVm getCategoryById(Integer id);

	List<CategoryGetVm> getCategories();

}
