package com.nhh203.productservice.controller;

import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.constant.PageableConstant;
import com.nhh203.productservice.exception.wrapper.BadRequestException;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.model.Brand;
import com.nhh203.productservice.viewmodel.brand.BrandListGetVm;
import com.nhh203.productservice.viewmodel.brand.BrandPostVm;
import com.nhh203.productservice.viewmodel.brand.BrandVm;
import com.nhh203.productservice.viewmodel.error.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nhh203.productservice.repository.BrandRepository;
import com.nhh203.productservice.service.BrandService;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BrandController {
	private final BrandRepository brandRepository;
	private final BrandService brandService;

	@GetMapping({"/backoffice/brands", "/storefront/brands"})
	public ResponseEntity<List<BrandVm>> listBrands() {
		log.info("[Test logging with trace] Got a request");
		List<BrandVm> brandVms = brandRepository.findAll().stream()
				.map(BrandVm::fromModel)
				.collect(java.util.stream.Collectors.toList());
		return ResponseEntity.ok(brandVms);
	}

	@GetMapping({"/backoffice/brands/paging", "/storefront/brands/paging"})
	public ResponseEntity<BrandListGetVm> getPageableBrands(
			@RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize) {
		return ResponseEntity.ok(brandService.getBrands(pageNo, pageSize));
	}

	@GetMapping("/backoffice/brands/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BrandVm.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<BrandVm> getBrand(@PathVariable("id") Long id) {
		Brand brand = brandRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, id));
		return ResponseEntity.ok(BrandVm.fromModel(brand));
	}

	@PostMapping("/backoffice/brands")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = BrandVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<BrandVm> createBrand(@Valid @RequestBody BrandPostVm brandPostVm,
	                                           UriComponentsBuilder uriComponentsBuilder) {
		Brand brand = brandService.create(brandPostVm);
		return ResponseEntity
				.created(uriComponentsBuilder.replacePath("/brands/{id}").buildAndExpand(brand.getId()).toUri())
				.body(BrandVm.fromModel(brand));
	}

	@PutMapping("/backoffice/brands/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content", content = @Content()),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> updateBrand(@PathVariable Long id, @Valid @RequestBody final BrandPostVm brandPostVm) {
		brandService.update(brandPostVm, id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/backoffice/brands/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content", content = @Content()),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> deleteBrand(@PathVariable long id) {
		Brand brand = brandRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, id));
		if (!brand.getProducts().isEmpty()) {
			throw new BadRequestException(Constants.ERROR_CODE.MAKE_SURE_BRAND_DONT_CONTAINS_ANY_PRODUCT);
		}
		brandRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
