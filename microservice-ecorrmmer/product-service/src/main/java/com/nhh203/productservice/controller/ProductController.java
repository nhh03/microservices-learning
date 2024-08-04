package com.nhh203.productservice.controller;


import com.nhh203.productservice.dto.req.ProductRequest;
import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.model.enumeration.FilterExistInWHSelection;
import com.nhh203.productservice.service.ProductService;
import com.nhh203.productservice.viewmodel.product.ProductDetailVm;
import com.nhh203.productservice.viewmodel.error.ErrorVm;
import com.nhh203.productservice.viewmodel.product.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {
	private final ProductService productService;

	@PostMapping(path = "/backoffice/products", consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductGetDetailVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<ProductGetDetailVm> createProduct(@Valid @RequestBody ProductPostVm productPostVm) {
		ProductGetDetailVm productGetDetailVm = productService.createProduct(productPostVm);
		return new ResponseEntity<>(productGetDetailVm, HttpStatus.CREATED);
	}

	@PutMapping(path = "/backoffice/products/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Updated"),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> updateProduct(@PathVariable String id, @Valid @RequestBody ProductPutVm productPutVm) {
		productService.updateProduct(id, productPutVm);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/backoffice/products")
	public ResponseEntity<ProductListGetVm> listProducts(
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "product-name", defaultValue = "", required = false) String productName,
			@RequestParam(value = "brand-name", defaultValue = "", required = false) String brandName) {
		return ResponseEntity.ok(productService.getProductsWithFilter(pageNo, pageSize, productName, brandName));
	}

	@GetMapping("/storefront/products/featured")
	public ResponseEntity<ProductFeatureGetVm> getFeaturedProducts(
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
		return ResponseEntity.ok(productService.getListFeaturedProducts(pageNo, pageSize));
	}

	@GetMapping("/storefront/brand/{brandSlug}/products")
	public ResponseEntity<List<ProductThumbnailVm>> getProductsByBrand(@PathVariable String brandSlug) {
		return ResponseEntity.ok(productService.getProductsByBrand(brandSlug));
	}

	@GetMapping({"/storefront/category/{categorySlug}/products", "/backoffice/category/{categorySlug}/products"})
	public ResponseEntity<ProductListGetFromCategoryVm> getProductsByCategory(
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "2", required = false) int pageSize,
			@PathVariable String categorySlug) {
		return ResponseEntity.ok(productService.getProductsFromCategory(pageNo, pageSize, categorySlug));
	}

	@GetMapping("/backoffice/products/{productId}")
	public ResponseEntity<ProductDetailVm> getProductById(@PathVariable String productId) {
		return ResponseEntity.ok(productService.getProductById(productId));
	}

	@GetMapping("/storefront/products/list-featured")
	public ResponseEntity<List<ProductThumbnailGetVm>> getFeaturedProductsById(
			@RequestParam("productId") List<String> productIds) {
		return ResponseEntity.ok(productService.getFeaturedProductsById(productIds));
	}

	@GetMapping("/storefront/product/{slug}")
	public ResponseEntity<ProductDetailGetVm> getProductDetail(@PathVariable("slug") String slug) {
		return ResponseEntity.ok(productService.getProductDetail(slug));
	}

	@DeleteMapping("/backoffice/products/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content", content = @Content()),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/storefront/products")
	public ResponseEntity<ProductsGetVm> getProductsByMultiQuery(
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "productName", defaultValue = "", required = false) String productName,
			@RequestParam(value = "categorySlug", defaultValue = "", required = false) String categorySlug,
			@RequestParam(value = "startPrice", defaultValue = "", required = false) Double startPrice,
			@RequestParam(value = "endPrice", defaultValue = "", required = false) Double endPrice) {
		return ResponseEntity.ok(productService.getProductsByMultiQuery(pageNo, pageSize, productName, categorySlug,
				startPrice, endPrice));
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Get product variations by parent id successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductVariationGetVm.class)))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class)))
	})
	@GetMapping({"/storefront/product-variations/{id}", "/backoffice/product-variations/{id}"})
	public ResponseEntity<List<ProductVariationGetVm>> getProductVariationsByParentId(@PathVariable String id) {
		return ResponseEntity.ok(productService.getProductVariationsByParentId(id));
	}

	@GetMapping("/storefront/productions/{id}/slug")
	public ResponseEntity<ProductSlugGetVm> getProductSlug(@PathVariable String id) {
		return ResponseEntity.ok(productService.getProductSlug(id));
	}

	@GetMapping("/storefront/products-es/{productId}")
	public ResponseEntity<ProductESDetailVm> getProductESDetailById(@PathVariable String productId) {
		return ResponseEntity.ok(productService.getProductESDetailById(productId));
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Get related products by product id successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductVariationGetVm.class)))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class)))
	})
	@GetMapping("/backoffice/products/related-products/{id}")
	public ResponseEntity<List<ProductListVm>> getRelatedProductsBackoffice(@PathVariable String id) {
		return ResponseEntity.ok(productService.getRelatedProductsBackoffice(id));
	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Get related products by product id successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductVariationGetVm.class)))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class)))
	})
	@GetMapping("/storefront/products/related-products/{id}")
	public ResponseEntity<ProductsGetVm> getRelatedProductsStorefront(
			@PathVariable String id,
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
		return ResponseEntity.ok(productService.getRelatedProductsStorefront(id, pageNo, pageSize));
	}

	@GetMapping("/backoffice/products/for-warehouse")
	public ResponseEntity<List<ProductInfoVm>> getProductsForWarehouse(
			@RequestParam String name, @RequestParam String sku,
			@RequestParam(required = false) List<Long> productIds,
			@RequestParam(required = false) FilterExistInWHSelection selection) {
		return ResponseEntity.ok(productService.getProductsForWarehouse(name, sku, productIds, selection));
	}

	@PutMapping(path = "/backoffice/products/update-quantity")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Updated"),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> updateProductQuantity(
			@Valid @RequestBody List<ProductQuantityPostVm> productQuantityPostVms) {
		productService.updateProductQuantity(productQuantityPostVms);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(path = "/backoffice/products/subtract-quantity", consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Updated"),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<Void> subtractProductQuantity(
			@Valid @RequestBody List<ProductQuantityPutVm> productQuantityPutVm) {
		productService.subtractStockQuantity(productQuantityPutVm);
		return ResponseEntity.noContent().build();
	}


	//	//	 TODO : create product
	//	@PostMapping("")
	//	@ResponseStatus(HttpStatus.CREATED)
	//	public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
	//		ProductResponse productResponse = productService.save(productRequest);
	//		if (productResponse != null) {
	//			return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
	//		}
	//		return new ResponseEntity<>("Thất bại", HttpStatus.INTERNAL_SERVER_ERROR);
	//	}
	//
	//	// TODO : get all products
	//	//	@GetMapping("/storefront/products")
	//	//	@ResponseStatus(HttpStatus.OK)
	//	//	public Flux<ProductResponse> getAllProducts() {
	//	//		return productService.getAllProducts();
	//	//	}
	//
	//	//TODO : get product by id
	//	@GetMapping("/{productId}")
	//	public ResponseEntity<ProductResponse> findById(@PathVariable("productId")
	//	                                                @NotBlank(message = "Input must not be blank!")
	//	                                                @Valid final String productId) {
	//		log.info("ProductDto, resource; fetch product by id");
	//		return ResponseEntity.ok(productService.findById(productId));
	//	}
	//
	//	// TODO: Update information of all product
	//	@PutMapping("/{productId}")
	//	public ResponseEntity<ProductResponse> update(@PathVariable("productId")
	//	                                              @NotBlank(message = "Input must not be blank!")
	//	                                              @Valid final String productId,
	//	                                              @RequestBody
	//	                                              @NotNull(message = "Input must not be NULL!")
	//	                                              @Valid final ProductRequest productRequest) {
	//		log.info("productRequest, resource; update product");
	//		return ResponseEntity.ok(productService.update(productId, productRequest));
	//	}
	//
	//
	//	//TODO: Delete a product
	//	@DeleteMapping("/{productId}")
	//	public ResponseEntity<?> deleteById(@PathVariable("productId") final String productId) {
	//		log.info("Boolean, resource; delete product by id");
	//		productService.deleteById(productId);
	//		return new ResponseEntity<>("Xóa thành công", HttpStatus.OK);
	//	}

}
