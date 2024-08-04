package com.nhh203.productservice.service;


import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.dto.req.ProductRequest;
import com.nhh203.productservice.model.enumeration.FilterExistInWHSelection;
import com.nhh203.productservice.viewmodel.product.ProductListGetFromCategoryVm;
import com.nhh203.productservice.viewmodel.product.ProductESDetailVm;
import com.nhh203.productservice.viewmodel.product.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.BiFunction;


public interface ProductService {
	Flux<ProductResponse> getAllProducts();

	ProductResponse findById(final String productId);

	ProductResponse save(final ProductRequest productDto);

	ProductResponse update(final ProductRequest productDto);

	ProductResponse update(final String productId, final ProductRequest productDto);

	void deleteById(final String productId);

	ProductGetDetailVm createProduct(ProductPostVm productPostVm);

	public void updateProduct(String productId, ProductPutVm productPutVm);

	public ProductListGetVm getProductsWithFilter(int pageNo, int pageSize, String productName, String brandName);

	List<ProductThumbnailVm> getProductsByBrand(String brandSlug);

	ProductFeatureGetVm getListFeaturedProducts(int pageNo, int pageSize);

	ProductDetailGetVm getProductDetail(String slug);

	void deleteProduct(String id);

	ProductsGetVm getProductsByMultiQuery(int pageNo, int pageSize, String productName, String categorySlug,
	                                      Double startPrice, Double endPrice);

	List<ProductVariationGetVm> getProductVariationsByParentId(String id);

	List<ProductExportingDetailVm> exportProducts(String productName, String brandName);

	ProductSlugGetVm getProductSlug(String id);


	ProductESDetailVm getProductESDetailById(String productId);

	List<ProductListVm> getRelatedProductsBackoffice(String id);

	ProductsGetVm getRelatedProductsStorefront(String id, int pageNo, int pageSize);

	List<ProductInfoVm> getProductsForWarehouse(
            String name, String sku, List<Long> productIds, FilterExistInWHSelection selection);

	void updateProductQuantity(List<ProductQuantityPostVm> productQuantityPostVms);

	void subtractStockQuantity(List<ProductQuantityPutVm> productQuantityItems);

	void restoreStockQuantity(List<ProductQuantityPutVm> productQuantityItems);

	void partitionUpdateStockQuantityByCalculation(List<ProductQuantityPutVm> productQuantityItems,
	                                               BiFunction<Long, Long, Long> calculation);

	ProductListGetFromCategoryVm getProductsFromCategory(int pageNo, int pageSize, String categorySlug);

	List<ProductThumbnailGetVm> getFeaturedProductsById(List<String> productIds);
	ProductDetailVm getProductById(String productId) ;
}

