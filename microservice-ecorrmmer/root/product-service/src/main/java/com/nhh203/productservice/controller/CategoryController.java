package com.nhh203.productservice.controller;


import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.dto.req.CategoryRequest;
import com.nhh203.productservice.dto.res.CategoryResponse;
import com.nhh203.productservice.exception.wrapper.BadRequestException;
import com.nhh203.productservice.model.Category;
import com.nhh203.productservice.repository.CategoryRepository;
import com.nhh203.productservice.service.CategoryService;
import com.nhh203.productservice.viewmodel.category.CategoryGetDetailVm;
import com.nhh203.productservice.viewmodel.category.CategoryGetVm;
import com.nhh203.productservice.viewmodel.category.CategoryPostVm;
import com.nhh203.productservice.viewmodel.error.ErrorVm;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
public class CategoryController {
	private final CategoryService categoryService;
	private final CategoryRepository categoryRepository;


	@Hidden
	// TODO : fetch all categories
	@GetMapping("/categories")
	public ResponseEntity<Flux<CategoryResponse>> findAll() {
		log.info("CategoryDto List, controller; fetch all categories");
		return ResponseEntity.ok(categoryService.findAll());
	}

	@GetMapping({"/backoffice/categories", "/storefront/categories"})
	public ResponseEntity<List<CategoryGetVm>> listCategories() {
		return ResponseEntity.ok(categoryService.getCategories());
	}


	@Hidden
	// TODO : create category
	@PostMapping("/categories")
	public ResponseEntity<Mono<CategoryResponse>> save(@RequestBody @NotNull(message = "Input must not be NULL")
	                                                   @Valid final CategoryRequest categoryRequest) {
		log.info("CategoryDto, resource; save category");
		return ResponseEntity.ok(categoryService.save(categoryRequest));
	}

	@Hidden
	// TODO: Get all list categories with paging
	@GetMapping("/categories/paging")
	public ResponseEntity<Page<CategoryResponse>> getAllCategories(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		Page<CategoryResponse> categoryPage = categoryService.findAllCategory(page, size);
		return new ResponseEntity<>(categoryPage, HttpStatus.OK);
	}

	@Hidden
	// TODO: Get all list categories with paging and sorting
	@GetMapping("/categories/paging-and-sorting")
	public ResponseEntity<List<CategoryResponse>> getAllEmployees(
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "categoryId") String sortBy) {

		List<CategoryResponse> list = categoryService.getAllCategories(pageNo, pageSize, sortBy);

		return new ResponseEntity<List<CategoryResponse>>(list, new HttpHeaders(), HttpStatus.OK);
	}

	@Hidden
	// TODO : fetch category by id
	@GetMapping("/categories/{categoryId}")
	public ResponseEntity<CategoryResponse> findById(@PathVariable("categoryId")
	                                                 @NotBlank(message = "Input must not be blank")
	                                                 @Valid final String categoryId) {
		log.info("CategoryDto, resource; fetch category by id");
		return ResponseEntity.ok(categoryService.findById(Integer.parseInt(categoryId)));
	}


	@Hidden
	// TODO: Update information of a category
	@PutMapping("/categories/{categoryId}")
	@Transactional
	public ResponseEntity<CategoryResponse> update(@PathVariable("categoryId")
	                                               @NotBlank(message = "Input must not be blank")
	                                               @Valid final String categoryId,
	                                               @RequestBody @NotNull(message = "Input must not be NULL")
	                                               @Valid final CategoryRequest categoryRequest) {
		log.info("CategoryDto, resource; update category with categoryId");
		return ResponseEntity.ok(categoryService.update(Integer.parseInt(categoryId), categoryRequest));
	}


	@Hidden
	// Delete a category
	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
		log.info("Boolean, resource; delete category by id");
		categoryService.deleteById(Integer.parseInt(categoryId));
		return ResponseEntity.ok(true);
	}

	@GetMapping("/backoffice/categories/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = CategoryGetDetailVm.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<CategoryGetDetailVm> getCategory(@PathVariable Integer id) {
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}

	@PostMapping("/backoffice/categories")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = CategoryGetDetailVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<CategoryGetDetailVm> createCategory(@Valid @RequestBody CategoryPostVm categoryPostVm, UriComponentsBuilder uriComponentsBuilder, Principal principal) {
		Category savedCategory = categoryService.create(categoryPostVm);
		CategoryGetDetailVm categoryGetDetailVm = CategoryGetDetailVm.fromModel(savedCategory);
		return ResponseEntity.created(uriComponentsBuilder.replacePath("/categories/{id}").buildAndExpand(savedCategory.getCategoryId()).toUri())
				.body(categoryGetDetailVm);
	}

	@PutMapping("/backoffice/categories/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content"),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> updateCategory(@PathVariable Integer id, @RequestBody @Valid final CategoryPostVm categoryPostVm, Principal principal) {
		categoryService.update(categoryPostVm, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/backoffice/categories/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content"),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, id));
		if (!category.getCategories().isEmpty()) {
			throw new BadRequestException(Constants.ERROR_CODE.MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_CHILDREN);
		}
		if (!category.getProductCategories().isEmpty()) {
			throw new BadRequestException(Constants.ERROR_CODE.MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_PRODUCT);
		}
		categoryRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
