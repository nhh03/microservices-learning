package com.nhh203.productservice.viewmodel.product;

import com.nhh203.productservice.model.Category;
import com.nhh203.productservice.viewmodel.ImageVm;
import lombok.ToString;

import java.util.List;

public record ProductDetailVm(String id,
                              String name,
                              String shortDescription,
                              String description,
                              String specification,
                              String sku,
                              String gtin,
                              String slug,
                              Boolean isAllowedToOrder,
                              Boolean isPublished,
                              Boolean isFeatured,
                              Boolean isVisible,
                              Boolean stockTrackingEnabled,
                              Double price,
                              Long brandId,
                              List<Category> categories,
                              String metaTitle,
                              String metaKeyword,
                              String metaDescription,
                              ImageVm thumbnailMedia,
                              List<ImageVm> productImageMedias,
                              Long taxClassId) {
}
