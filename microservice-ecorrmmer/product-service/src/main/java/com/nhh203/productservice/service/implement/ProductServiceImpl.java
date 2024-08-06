package com.nhh203.productservice.service.implement;

import com.google.gson.Gson;
import com.nhh203.productservice.Utils.BeanUtilsUpdate;
import com.nhh203.productservice.Utils.Constants;
import com.nhh203.productservice.Utils.StringUtils;
import com.nhh203.productservice.dto.req.ProductRequest;
import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.exception.wrapper.BadRequestException;
import com.nhh203.productservice.exception.wrapper.CategoryNotFoundException;
import com.nhh203.productservice.exception.wrapper.NotFoundException;
import com.nhh203.productservice.exception.wrapper.ProductNotFoundException;
import com.nhh203.productservice.helper.ProductMappingHelper;
import com.nhh203.productservice.model.*;
import com.nhh203.productservice.model.attribute.ProductAttributeGroup;
import com.nhh203.productservice.model.attribute.ProductAttributeValue;
import com.nhh203.productservice.model.enumeration.FilterExistInWHSelection;
import com.nhh203.productservice.repository.*;
import com.nhh203.productservice.service.MediaService;
import com.nhh203.productservice.service.ProductService;
import com.nhh203.productservice.viewmodel.product.*;
import com.nhh203.productservice.viewmodel.ImageVm;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeGroupGetVm;
import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeValueVm;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import org.apache.commons.collections4.CollectionUtils;


import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
	private static final String NONE_GROUP = "None group";
	ProductRepository productRepository;
	CategoryRepository categoryRepository;
	BrandRepository brandRepository;
	ProductImageRepository productImageRepository;
	ProductRelatedRepository productRelatedRepository;
	ProductOptionRepository productOptionRepository;
	ProductOptionCombinationRepository productOptionCombinationRepository;
	ProductOptionValueRepository productOptionValueRepository;
	ProductCategoryRepository productCategoryRepository;
	MediaService mediaService;
	ModelMapper modelMapper;
	Gson gson;

	private static void setValuesForVariantExisting(List<ProductImage> newProductImages, ProductVariationPutVm variant, Product variantInDB) {
		if (variantInDB != null) {
			variantInDB.setProductTitle(variant.name());
			variantInDB.setSlug(variant.slug());
			variantInDB.setSku(variant.sku());
			variantInDB.setGtin(variant.gtin());
			variantInDB.setPrice(BigDecimal.valueOf(variant.price()));
			variantInDB.setThumbnailMediaId(variant.thumbnailMediaId());
			List<ProductImage> productImages = variantInDB.getProductImages();
			if (CollectionUtils.isNotEmpty(variant.productImageIds())) {
				variant.productImageIds().forEach(imageId -> {
					if (productImages.stream().noneMatch(pImage -> imageId.equals(pImage.getImageId()))) {
						newProductImages.add(ProductImage.builder().imageId(imageId).product(variantInDB).build());
					}
				});
			}
		}
	}

	private static Function<Product, ProductOptionCombination> mapToOptionCombination(Map<Long, ProductOption> productOptionMap, Long optionValue, String value, Integer optionValue1) {
		return variant -> ProductOptionCombination.builder().product(variant).productOption(productOptionMap.get(optionValue)).value(value).displayOrder(optionValue1).build();
	}

	@Override
	public Flux<ProductResponse> getAllProducts() {
		log.info("ProductDto List, service, fetch all products");
		return Flux.fromIterable(this.productRepository.findAll()).map(ProductMappingHelper::mapToProductResponse).distinct().onErrorResume(throwable -> {
			log.error("Error while fetching product: " + throwable.getMessage());
			return Flux.empty();
		});
	}

	@Override
	public ProductResponse findById(String productId) {
		log.info("ProductDto, service; fetch product by id");
		Optional<Product> product = this.productRepository.findById(productId);
		if (product.isEmpty()) {
			log.error("Product with id {} not found", productId);
			return null;
		}
		return ProductMappingHelper.mapToProductResponse(product.get());
	}

	@Deprecated
	@Override
	public ProductResponse save(ProductRequest productRequest) {
		log.info("ProductDto, service; save product");
		Optional<Category> category = this.categoryRepository.findById(productRequest.getCategoryId());
		if (category.isEmpty()) {
			log.error("Category with id {} not found", productRequest.getCategoryId());
			throw new CategoryNotFoundException("Not found category id : productRequest.getCategoryId())");
		}
		Product product = modelMapper.map(productRequest, Product.class);
		product.setCategory(category.get());
		this.productRepository.save(product);
		return ProductMappingHelper.mapToProductResponse(product);
	}

	@Override
	public ProductResponse update(ProductRequest productDto) {
		return null;
	}

	@Override
	public ProductResponse update(String productId, ProductRequest productRequest) {
		log.info("productRequest, service; update product by id");
		try {
			Product existingProduct = this.productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
			if (productRequest.getCategoryId() != null) {
				Optional<Category> category = this.categoryRepository.findById(productRequest.getCategoryId());
				if (category.isEmpty()) {
					log.error("Category with id {} not found", productRequest.getCategoryId());
					throw new CategoryNotFoundException("Not found category id : productRequest.getCategoryId())");
				}
				existingProduct.setCategory(category.get());
			}
			BeanUtilsUpdate.copyNonNullProperties(productRequest, existingProduct);
			this.productRepository.save(existingProduct);
			return ProductMappingHelper.mapToProductResponse(existingProduct);
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new ProductNotFoundException("Error update product", exception);
		}
	}

	@Override
	public void deleteById(String productId) {
		log.info("Void, service; delete product by id");
		try {
			Optional<Product> product = this.productRepository.findById(productId);
			if (product.isEmpty()) {
				log.error("Product with id {} not found", productId);
				throw new ProductNotFoundException("Not found product id :" + productId);
			}
			this.productRepository.deleteById(String.valueOf(productId));

		} catch (Exception e) {
			throw new ProductNotFoundException("Error delete product", e);
		}
	}

	@Override
	public ProductGetDetailVm createProduct(ProductPostVm productPostVm) {
		Product mainProduct = Product.builder().productTitle(productPostVm.name()).slug(productPostVm.slug()).description(productPostVm.description()).shortDescription(productPostVm.shortDescription()).specification(productPostVm.specification()).sku(productPostVm.sku()).gtin(productPostVm.gtin()).price(BigDecimal.valueOf(productPostVm.price())).isAllowedToOrder(productPostVm.isAllowedToOrder()).isPublished(productPostVm.isPublished()).isFeatured(productPostVm.isFeatured()).isVisibleIndividually(productPostVm.isVisibleIndividually()).stockTrackingEnabled(productPostVm.stockTrackingEnabled()).taxIncluded(productPostVm.taxIncluded()).metaTitle(productPostVm.metaTitle()).metaKeyword(productPostVm.metaKeyword()).metaDescription(productPostVm.description()).hasOptions(CollectionUtils.isNotEmpty(productPostVm.variations()) && CollectionUtils.isNotEmpty(productPostVm.productOptionValues())).productCategories(List.of()).taxClassId(productPostVm.taxClassId()).build();
		setProductBrand(productPostVm.brandId(), mainProduct);
		List<ProductCategory> productCategories = setProductCategories(productPostVm.categoryIds(), mainProduct);
		mainProduct.setProductCategories(productCategories);

		List<ProductImage> productImageList = setProductImages(productPostVm.productImageIds(), mainProduct);

		Product mainSavedProduct = productRepository.saveAndFlush(mainProduct);
		productImageRepository.saveAllAndFlush(productImageList);

		// Save related products
		if (CollectionUtils.isNotEmpty(productPostVm.relatedProductIds())) {
			List<Product> relatedProducts = productRepository.findAllById(productPostVm.relatedProductIds());
			List<ProductRelated> productRelatedList = relatedProducts.stream().map(relatedProduct -> ProductRelated.builder().product(mainSavedProduct).relatedProduct(relatedProduct).build()).toList();
			productRelatedRepository.saveAllAndFlush(productRelatedList);
		}

		// save product variations , product option values , and product options combinations
		if (CollectionUtils.isNotEmpty(productPostVm.variations()) && CollectionUtils.isNotEmpty(productPostVm.productOptionValues())) {
			List<ProductImage> allProductVariantImageList = new ArrayList<>();
			List<Product> productVariants = productPostVm.variations().stream().map(variation -> {
				Product productVariant = Product.builder().productTitle(variation.name()).thumbnailMediaId(variation.thumbnailMediaId()).slug(variation.slug().toLowerCase()).sku(variation.sku()).gtin(variation.gtin()).price(BigDecimal.valueOf(variation.price())).isPublished(productPostVm.isPublished()).parent(mainSavedProduct).build();
				List<ProductImage> productVariantImageList = setProductImages(variation.productImageIds(), productVariant);
				if (productVariantImageList != null) {
					allProductVariantImageList.addAll(productVariantImageList);
				}
				return productVariant;
			}).toList();
			List<Product> productsVariantsSaved = productRepository.saveAllAndFlush(productVariants);
			productImageRepository.saveAllAndFlush(allProductVariantImageList);

			List<Long> productOptionsId = productPostVm.productOptionValues().stream().map(ProductOptionValuePostVm::productOptionId).toList();
			List<ProductOption> productOptions = productOptionRepository.findAllById(productOptionsId);
			Map<Long, ProductOption> productOptionMap = productOptions.stream().collect(Collectors.toMap(ProductOption::getId, Function.identity()));
			List<ProductOptionValue> productOptionValues = new ArrayList<>();
			List<ProductOptionCombination> productOptionCombinations = new ArrayList<>();

			productPostVm.productOptionValues().forEach(optionValue -> optionValue.value().forEach(value -> {
				ProductOptionValue productOptionValue = ProductOptionValue.builder()
						.product(mainSavedProduct)
						.displayOrder(optionValue.displayOrder())
						.displayType(optionValue.displayType())
						.productOption(productOptionMap.get(optionValue.productOptionId())).value(value)
						.build();
				List<ProductOptionCombination> productOptionCombinationList = new ArrayList<>();

				for (Product variant : productsVariantsSaved) {
					if (variant.getSlug().contains(StringUtils.toSlug(value))) {
						ProductOptionCombination combination = ProductOptionCombination.builder().product(variant).productOption(productOptionMap.get(optionValue.productOptionId())).value(value).displayOrder(optionValue.displayOrder()).build();
						productOptionCombinationList.add(combination);
					}
				}
				productOptionValues.add(productOptionValue);
				productOptionCombinations.addAll(productOptionCombinationList);
			}));

			productOptionValueRepository.saveAllAndFlush(productOptionValues);
			productOptionCombinationRepository.saveAllAndFlush(productOptionCombinations);
		}
		return ProductGetDetailVm.fromModel(mainSavedProduct);
	}

	@Override
	public void updateProduct(String productId, ProductPutVm productPutVm) {
		Product product = this.productRepository.findById(productId).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
		setProductBrand(productPutVm.brandId(), product);

		List<ProductCategory> productCategoryList = setProductCategories(productPutVm.categoryIds(), product);
		List<ProductImage> productImages = setProductImages(productPutVm.productImageIds(), product);

		product.setProductTitle(productPutVm.name());
		product.setSlug(productPutVm.slug());
		product.setThumbnailMediaId(productPutVm.thumbnailMediaId());
		product.setDescription(productPutVm.description());
		product.setShortDescription(productPutVm.shortDescription());
		product.setSpecification(productPutVm.specification());
		product.setSku(productPutVm.sku());
		product.setGtin(productPutVm.gtin());
		product.setPrice(BigDecimal.valueOf(productPutVm.price()));
		product.setAllowedToOrder(productPutVm.isAllowedToOrder());
		product.setFeatured(productPutVm.isFeatured());
		product.setPublished(productPutVm.isPublished());
		product.setVisibleIndividually(productPutVm.isVisibleIndividually());
		product.setStockTrackingEnabled(productPutVm.stockTrackingEnabled());
		product.setMetaTitle(productPutVm.metaTitle());
		product.setMetaKeyword(productPutVm.metaKeyword());
		product.setMetaDescription(productPutVm.metaDescription());
		product.setTaxClassId(productPutVm.taxClassId());

		List<ProductImage> newProductImages = new ArrayList<ProductImage>();
		List<Product> exitingVariants = product.getProducts();

		updateExistingVariants(productPutVm, newProductImages, exitingVariants);

		// handle update related product
		List<ProductRelated> newProductRelatedList = new ArrayList<>();
		List<ProductRelated> removeProductRelatedList = new ArrayList<>();
		List<String> newRelatedProductIds = productPutVm.relatedProductIds();
		List<ProductRelated> oldRelatedProducts = product.getRelatedProducts();
		Set<String> oldRelatedProductIds = oldRelatedProducts.stream().map(ProductRelated::getRelatedProduct).map(Product::getProductId).collect(Collectors.toSet());
		Set<String> removeRelatedProductIds = oldRelatedProductIds.stream().filter(id -> !newRelatedProductIds.contains(id)).collect(Collectors.toSet());
		Set<String> addRelatedProductIds = newRelatedProductIds.stream().filter(id -> !oldRelatedProductIds.contains(id)).collect(Collectors.toSet());

		removeProductRelatedList = oldRelatedProducts.stream().filter(relatedProduct -> {
			return removeRelatedProductIds.contains(relatedProduct.getRelatedProduct().getProductId());
		}).toList();
		List<Product> addRelatedProducts = productRepository.findAllById(addRelatedProductIds);
		newProductRelatedList = addRelatedProducts.stream().map(addRelatedProduct -> ProductRelated.builder().product(product).relatedProduct(addRelatedProduct).build()).toList();

		this.productRepository.saveAndFlush(product);

		List<ProductCategory> productCategories = productCategoryRepository.findAllByProductProductId(productId);
		this.productCategoryRepository.deleteAllInBatch(productCategories);
		this.productCategoryRepository.saveAllAndFlush(productCategoryList);

		this.productRelatedRepository.deleteAll(removeProductRelatedList);
		this.productRelatedRepository.saveAllAndFlush(newProductRelatedList);

	}

	@Override
	public ProductListGetVm getProductsWithFilter(int pageNo, int pageSize, String productName, String brandName) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> productPage = this.productRepository.getProductsWithFilter(productName.trim().toLowerCase(), brandName.trim().toLowerCase(), pageable);
		List<ProductListVm> productContent = productPage.getContent().stream().map(ProductListVm::fromModel).toList();
		return new ProductListGetVm(productContent, productPage.getNumber(), productPage.getSize(), (int) productPage.getTotalElements(), productPage.getTotalPages(), productPage.isLast());
	}

	@Override
	public List<ProductThumbnailVm> getProductsByBrand(String brandSlug) {
		List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
		Brand brand = brandRepository.findBySlug(brandSlug).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, brandSlug));
		List<Product> products = productRepository.findAllByBrandAndIsPublishedTrue(brand);
		for (Product product : products) {
			productThumbnailVms.add(new ProductThumbnailVm(product.getProductId(), product.getProductTitle(), product.getSlug(), ""
					//					mediaService.getMedia(product.getThumbnailMediaId()).url()
			));
		}
		return productThumbnailVms;
	}

	@Override
	public ProductFeatureGetVm getListFeaturedProducts(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		List<ProductThumbnailGetVm> productThumbnailVms = new ArrayList<>();
		Page<Product> productPage = productRepository.getFeaturedProduct(pageable);
		List<Product> products = productPage.getContent();
		for (Product product : products) {
			productThumbnailVms.add(new ProductThumbnailGetVm(
					product.getProductId(),
					product.getProductTitle(),
					product.getSlug(),
					mediaService.getMedia(product.getThumbnailMediaId()).url(),
					product.getPrice().doubleValue()));
		}
		return new ProductFeatureGetVm(productThumbnailVms, productPage.getTotalPages());
	}

	@Override
	public ProductDetailGetVm getProductDetail(String slug) {
		Product product = productRepository.findBySlugAndIsPublishedTrue(slug)
				.orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, slug));
		Long productThumbnailMediaId = product.getThumbnailMediaId();
		String productThumbnailUrl = mediaService.getMedia(productThumbnailMediaId).url();
		List<String> productImageMediaUrls = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(product.getProductImages())) {
			for (ProductImage image : product.getProductImages()) {
				productImageMediaUrls.add(mediaService.getMedia(image.getImageId()).url());
			}
		}
		List<ProductAttributeGroupGetVm> productAttributeGroupsVm = new ArrayList<>();
		List<ProductAttributeValue> productAttributeValues = product.getAttributeValues();
		if (CollectionUtils.isNotEmpty(productAttributeValues)) {
			List<ProductAttributeGroup> productAttributeGroups = productAttributeValues
					.stream()
					.map(productAttributeValue -> productAttributeValue
							.getProductAttribute()
							.getProductAttributeGroup())
					.distinct()
					.toList();
			productAttributeGroups.forEach(productAttributeGroup -> {
				List<ProductAttributeValueVm> productAttributeValueVms = new ArrayList<>();
				productAttributeValues.forEach(productAttributeValue -> {
					ProductAttributeGroup group = productAttributeValue.getProductAttribute().getProductAttributeGroup();
					if ((group != null && group.equals(productAttributeGroup)) || (group == null && productAttributeGroup == null)) {
						ProductAttributeValueVm productAttributeValueVm = new ProductAttributeValueVm(productAttributeValue.getProductAttribute().getName(), productAttributeValue.getValue());
						productAttributeValueVms.add(productAttributeValueVm);
					}
				});
				String productAttributeGroupName = productAttributeGroup == null ? NONE_GROUP : productAttributeGroup.getName();
				ProductAttributeGroupGetVm productAttributeGroupVm = new ProductAttributeGroupGetVm(productAttributeGroupName, productAttributeValueVms);
				productAttributeGroupsVm.add(productAttributeGroupVm);
			});
		}

		return new ProductDetailGetVm(
				product.getProductId(),
				product.getProductTitle(),
				product.getBrand() == null ? null : product.getBrand().getName(),
				product.getProductCategories().stream().map(category -> category.getCategory().getCategoryTitle()).toList(),
				productAttributeGroupsVm,
				product.getShortDescription(),
				product.getDescription(),
				product.getSpecification(),
				product.isAllowedToOrder(),
				product.isPublished(),
				product.isFeatured(),
				product.isHasOptions(),
				product.getPrice().doubleValue(),
				productThumbnailUrl,
				productImageMediaUrls);
	}

	@Override
	public void deleteProduct(String id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
		product.setPublished(false);
		productRepository.save(product);
	}

	@Override
	public ProductsGetVm getProductsByMultiQuery(int pageNo, int pageSize, String productName, String categorySlug, Double startPrice, Double endPrice) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> productPage;
		productPage = productRepository.findByProductNameAndCategorySlugAndPriceBetween(productName.trim().toLowerCase(), categorySlug.trim(), startPrice, endPrice, pageable);
		List<ProductThumbnailGetVm> productThumbnailVms = new ArrayList<>();
		List<Product> products = productPage.getContent();
		for (Product product : products) {
			productThumbnailVms.add(new ProductThumbnailGetVm(
					product.getProductId(),
					product.getProductTitle(), product.getSlug(),
					mediaService.getMedia(product.getThumbnailMediaId()).url(),
					product.getPrice().doubleValue()));
		}
		return new ProductsGetVm(productThumbnailVms, productPage.getNumber(), productPage.getSize(), (int) productPage.getTotalElements(), productPage.getTotalPages(), productPage.isLast());
	}

	@Override
	public List<ProductVariationGetVm> getProductVariationsByParentId(String id) {
		Product parentProduct = productRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
		if (Boolean.TRUE.equals(parentProduct.isHasOptions())) {
			List<Product> productVariations = parentProduct.getProducts()
					.stream()
					.filter(Product::isPublished)
					.toList();

			return productVariations.stream().map(product -> {
				List<ProductOptionCombination> productOptionCombinations = productOptionCombinationRepository.findAllByProduct(product);
				Map<String, String> options = productOptionCombinations
						.stream()
						.collect(Collectors.toMap(productOptionCombination -> productOptionCombination
								.getProductOption().getName(), ProductOptionCombination::getValue));
				return new ProductVariationGetVm(
						product.getProductId(),
						product.getProductTitle(),
						product.getSlug(),
						product.getSku(),
						product.getGtin(),
						product.getPrice(),
						new ImageVm(product.getThumbnailMediaId(),
								mediaService.getMedia(product.getThumbnailMediaId()).url()),
						product.getProductImages()
								.stream()
								.map(productImage -> new ImageVm(
										productImage.getImageId(),
										mediaService.getMedia(productImage.getImageId()).url())
								).toList(), options);
			}).toList();
		} else {
			throw new BadRequestException(Constants.ERROR_CODE.PRODUCT_NOT_HAVE_VARIATION, id);
		}
	}

	@Override
	public List<ProductExportingDetailVm> exportProducts(String productName, String brandName) {
		return null;
	}

	@Override
	public ProductSlugGetVm getProductSlug(String id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
		Product parent = product.getParent();
		if (parent != null) {
			return new ProductSlugGetVm(parent.getSlug(), id);
		}
		return new ProductSlugGetVm(product.getSlug(), null);
	}

	@Override
	public ProductESDetailVm getProductESDetailById(String productId) {
		Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
		Long thumbnailMediaId = null;
		if (null != product.getThumbnailMediaId()) {
			thumbnailMediaId = product.getThumbnailMediaId();
		}
		List<String> categoryNames = product.getProductCategories().stream().map(productCategory -> productCategory.getCategory().getCategoryTitle()).toList();
		List<String> attributeNames = product.getAttributeValues().stream().map(attributeValue -> attributeValue.getProductAttribute().getName()).toList();
		String brandName = null;
		if (null != product.getBrand()) {
			brandName = product.getBrand().getName();
		}
		return new ProductESDetailVm(product.getProductId(), product.getProductTitle(), product.getSlug(), product.getPrice(), product.isPublished(), product.isVisibleIndividually(), product.isAllowedToOrder(), product.isFeatured(), thumbnailMediaId, brandName, categoryNames, attributeNames);
	}

	@Override
	public List<ProductListVm> getRelatedProductsBackoffice(String id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
		List<ProductRelated> relatedProducts = product.getRelatedProducts();
		return relatedProducts.stream().map(productRelated -> new ProductListVm(productRelated.getRelatedProduct().getProductId(), productRelated.getRelatedProduct().getProductTitle(), productRelated.getRelatedProduct().getSlug(), productRelated.getRelatedProduct().isAllowedToOrder(), productRelated.getRelatedProduct().isPublished(), productRelated.getRelatedProduct().isFeatured(), productRelated.getRelatedProduct().isVisibleIndividually(), productRelated.getRelatedProduct().getCreateAt().toString(), productRelated.getRelatedProduct().getTaxClassId())).toList();
	}

	@Override
	public ProductsGetVm getRelatedProductsStorefront(String id, int pageNo, int pageSize) {
		Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, id));
		Page<ProductRelated> relatedProductsPage = productRelatedRepository.findAllByProduct(product, PageRequest.of(pageNo, pageSize));
		List<ProductThumbnailGetVm> productThumbnailVms = relatedProductsPage.stream().filter(productRelated -> productRelated.getRelatedProduct().isPublished()).map(productRelated -> {
			Product relatedProduct = productRelated.getRelatedProduct();
			return new ProductThumbnailGetVm(relatedProduct.getProductId(), relatedProduct.getProductTitle(), relatedProduct.getSlug(), mediaService.getMedia(relatedProduct.getThumbnailMediaId()).url(), relatedProduct.getPrice().doubleValue());
		}).toList();
		return new ProductsGetVm(productThumbnailVms, relatedProductsPage.getNumber(), relatedProductsPage.getSize(), (int) relatedProductsPage.getTotalElements(), relatedProductsPage.getTotalPages(), relatedProductsPage.isLast());
	}

	@Override
	public List<ProductInfoVm> getProductsForWarehouse(String name, String sku, List<Long> productIds, FilterExistInWHSelection selection) {
		return productRepository.findProductForWarehouse(name, sku, productIds, selection.name()).stream().map(ProductInfoVm::fromProduct).toList();
	}

	@Override
	public void updateProductQuantity(List<ProductQuantityPostVm> productQuantityPostVms) {
		List<String> productIds = productQuantityPostVms.stream().map(ProductQuantityPostVm::productId).toList();
		List<Product> products = productRepository.findAllByProductIdIn(productIds);
		products.parallelStream().forEach(product -> {
			Optional<ProductQuantityPostVm> productQuantityPostVmOptional = productQuantityPostVms.parallelStream().filter(productPostVm -> product.getProductId().equals(productPostVm.productId())).findFirst();
			productQuantityPostVmOptional.ifPresent(productQuantityPostVm -> product.setStockQuantity(productQuantityPostVm.stockQuantity()));
		});
		productRepository.saveAll(products);
	}

	@Override
	public void subtractStockQuantity(List<ProductQuantityPutVm> productQuantityItems) {
		ListUtils.partition(productQuantityItems, 5).forEach(it -> partitionUpdateStockQuantityByCalculation(it, this.subtractStockQuantity()));
	}

	@Override
	public void restoreStockQuantity(List<ProductQuantityPutVm> productQuantityItems) {
		ListUtils.partition(productQuantityItems, 5).forEach(it -> partitionUpdateStockQuantityByCalculation(it, this.restoreStockQuantity()));
	}

	@Override
	public void partitionUpdateStockQuantityByCalculation(List<ProductQuantityPutVm> productQuantityItems, BiFunction<Long, Long, Long> calculation) {
		var productIds = productQuantityItems.stream().map(ProductQuantityPutVm::productId).toList();

		var productQuantityItemMap = productQuantityItems.stream().collect(Collectors.toMap(ProductQuantityPutVm::productId, Function.identity(), this::mergeProductQuantityItem));

		List<Product> products = this.productRepository.findAllByProductIdIn(productIds);
		products.forEach(product -> {
			if (product.isStockTrackingEnabled()) {
				long amount = getRemainAmountOfStockQuantity(productQuantityItemMap, product, calculation);
				product.setStockQuantity(amount);
			}
		});
		this.productRepository.saveAll(products);
	}

	@Override
	public ProductListGetFromCategoryVm getProductsFromCategory(int pageNo, int pageSize, String categorySlug) {
		List<ProductThumbnailVm> productThumbnailVms = new ArrayList<>();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Category category = categoryRepository.findBySlug(categorySlug).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, categorySlug));
		Page<ProductCategory> productCategoryPage = productCategoryRepository.findAllByCategory(pageable, category);
		List<ProductCategory> productList = productCategoryPage.getContent();
		List<Product> products = productList.stream().map(ProductCategory::getProduct).toList();
		for (Product product : products) {
			productThumbnailVms.add(new ProductThumbnailVm(product.getProductId(), product.getProductTitle(), product.getSlug(), "")
					//					mediaService.getMedia(product.getThumbnailMediaId()).url())
			);
		}
		return new ProductListGetFromCategoryVm(productThumbnailVms, productCategoryPage.getNumber(), productCategoryPage.getSize(), (int) productCategoryPage.getTotalElements(), productCategoryPage.getTotalPages(), productCategoryPage.isLast());
	}

	@Override
	public List<ProductThumbnailGetVm> getFeaturedProductsById(List<String> productIds) {
		List<Product> products = productRepository.findAllByProductIdIn(productIds);
		return products.stream().map(product -> new ProductThumbnailGetVm(
				product.getProductId(),
				product.getProductTitle(),
				product.getSlug(),
				mediaService.getMedia(product.getThumbnailMediaId()).url(),
				product.getPrice().doubleValue())).toList();
	}

	@Override
	public ProductDetailVm getProductById(String productId) {
		Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
		List<ImageVm> productImageMedias = new ArrayList<>();
		if (null != product.getProductImages()) {
			for (ProductImage image : product.getProductImages()) {
				productImageMedias.add(new ImageVm(image.getImageId(), mediaService.getMedia(image.getImageId()).url()));
			}
		}
		ImageVm thumbnailMedia = null;
		if (null != product.getThumbnailMediaId()) {
			thumbnailMedia = new ImageVm(product.getThumbnailMediaId(), mediaService.getMedia(product.getThumbnailMediaId()).url());
		}
		List<Category> categories = new ArrayList<>();
		if (null != product.getProductCategories()) {
			for (ProductCategory category : product.getProductCategories()) {
				categories.add(category.getCategory());
			}
		}
		Long brandId = null;
		if (null != product.getBrand()) {
			brandId = product.getBrand().getId();
		}
		return new ProductDetailVm(product.getProductId(), product.getProductTitle(), product.getShortDescription(), product.getDescription(), product.getSpecification(), product.getSku(), product.getGtin(), product.getSlug(), product.isAllowedToOrder(), product.isPublished(), product.isFeatured(), product.isVisibleIndividually(), product.isStockTrackingEnabled(), product.getPrice().doubleValue(), brandId, categories, product.getMetaTitle(), product.getMetaKeyword(), product.getMetaDescription(), thumbnailMedia, productImageMedias, product.getTaxClassId());
	}

	private void updateExistingVariants(ProductPutVm productPutVm, List<ProductImage> newProductImages, List<Product> existingVariants) {
		if (CollectionUtils.isNotEmpty(productPutVm.variations())) {
			productPutVm.variations().forEach(variant -> {
				if (variant.id() != null) {
					Product variantInDB = existingVariants.stream().filter(pVariant -> variant.id().toString().equals(pVariant.getProductId())).findFirst().orElse(null);
					setValuesForVariantExisting(newProductImages, variant, variantInDB);
				}
			});
		}
	}

	private BiFunction<Long, Long, Long> subtractStockQuantity() {
		return (totalQuantity, amount) -> {
			long result = totalQuantity - amount;
			return result < 0 ? 0 : result;
		};
	}

	private BiFunction<Long, Long, Long> restoreStockQuantity() {
		return Long::sum;
	}

	private List<ProductImage> setProductImages(List<Long> imageMediaIds, Product mainProduct) {
		List<ProductImage> productImages = new ArrayList<>();
		if (CollectionUtils.isEmpty(imageMediaIds)) {
			return productImages;
		}
		if (mainProduct.getProductImages() == null) {
			productImages = imageMediaIds.stream().map(id -> ProductImage.builder().product(mainProduct).imageId(id).build()).toList();
		} else {
			List<Long> existingImageIds = mainProduct.getProductImages().stream().map(ProductImage::getImageId).toList();
			List<Long> newImageIds = imageMediaIds.stream().filter(id -> !existingImageIds.contains(id)).toList();
			List<Long> deleteImageIds = existingImageIds.stream().filter(id -> !imageMediaIds.contains(id)).toList();
			if (CollectionUtils.isNotEmpty(newImageIds)) {
				productImages = newImageIds.stream().map(id -> ProductImage.builder().product(mainProduct).imageId(id).build()).toList();
			} else {
				productImageRepository.deleteByImageIdInAndProduct(deleteImageIds, mainProduct);
			}
		}
		return productImages;

	}

	private List<ProductCategory> setProductCategories(List<Integer> categoryIds, Product product) {
		List<ProductCategory> productCategories = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(categoryIds)) {
			List<Category> categoryList = categoryRepository.findAllById(categoryIds);
			if (categoryList.isEmpty()) {
				throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, "Category not found");
			} else if (categoryList.size() < categoryIds.size()) {
				throw new BadRequestException(Constants.ERROR_CODE.CATEGORY_NOT_FOUND, "Some category not found");
			} else {
				for (Category category : categoryList) {
					ProductCategory productCategory = ProductCategory.builder().product(product).category(category).build();
					productCategories.add(productCategory);
				}
			}
		}
		return productCategories;
	}

	private void setProductBrand(Long brandId, Product product) {
		if (brandId != null && (product.getBrand() == null || !(brandId.equals(product.getBrand().getId())))) {
			Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new BadRequestException(Constants.ERROR_CODE.BRAND_NOT_FOUND, "Brand not found"));
			product.setBrand(brand);
		}
	}

	private ProductQuantityPutVm mergeProductQuantityItem(ProductQuantityPutVm p1, ProductQuantityPutVm p2) {
		var q1 = p1.quantity();
		var q2 = p2.quantity();
		return new ProductQuantityPutVm(p1.productId(), q1 + q2);
	}

	private Long getRemainAmountOfStockQuantity(Map<String, ProductQuantityPutVm> productQuantityItemMap, Product product, BiFunction<Long, Long, Long> calculation) {
		Long stockQuantity = product.getStockQuantity();
		var productItem = productQuantityItemMap.get(product.getProductId());
		Long quantity = productItem.quantity();
		return calculation.apply(stockQuantity, quantity);
	}

}
