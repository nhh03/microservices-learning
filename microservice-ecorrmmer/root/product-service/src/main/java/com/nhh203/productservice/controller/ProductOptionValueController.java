package com.nhh203.productservice.controller;

import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.model.Product;
import com.nhh203.productservice.repository.ProductOptionValueRepository;
import com.nhh203.productservice.repository.ProductRepository;
import com.nhh203.productservice.viewmodel.error.ErrorVm;
import com.nhh203.productservice.viewmodel.productoption.ProductOptionValueGetVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductOptionValueController {
	private final ProductOptionValueRepository productOptionValueRepository;
	private final ProductRepository productRepository;

	public ProductOptionValueController(ProductOptionValueRepository productOptionValueRepository, ProductRepository productRepository) {
		this.productOptionValueRepository = productOptionValueRepository;
		this.productRepository = productRepository;
	}

	@GetMapping({"/backoffice/product-option-values"})
	public ResponseEntity<List<com.nhh203.productservice.viewmodel.productoption.ProductOptionValueGetVm>> listProductOptionValues() {
		List<com.nhh203.productservice.viewmodel.productoption.ProductOptionValueGetVm> productOptionGetVms = productOptionValueRepository
				.findAll().stream()
				.map(com.nhh203.productservice.viewmodel.productoption.ProductOptionValueGetVm::fromModel)
				.toList();
		return ResponseEntity.ok(productOptionGetVms);
	}

	@GetMapping({"/storefront/product-option-values/{productId}"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = com.nhh203.productservice.viewmodel.productoption.ProductOptionValueGetVm.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
	})
	public ResponseEntity<List<ProductOptionValueGetVm>> listProductOptionValueOfProduct(@PathVariable("productId") String productId) {
		Product product = productRepository
				.findById(productId)
				.orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
		List<ProductOptionValueGetVm> productVariations = productOptionValueRepository
				.findAllByProduct(product).stream()
				.map(ProductOptionValueGetVm::fromModel)
				.toList();
		return ResponseEntity.ok(productVariations);
	}
}
