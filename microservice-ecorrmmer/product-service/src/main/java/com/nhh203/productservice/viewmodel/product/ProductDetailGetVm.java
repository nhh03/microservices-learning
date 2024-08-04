package com.nhh203.productservice.viewmodel.product;

import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeGroupGetVm;

import java.util.List;

public record ProductDetailGetVm(
        String id,
        String name,
        String brandName,
        List<String> productCategories,
        List<ProductAttributeGroupGetVm> productAttributeGroups,
        String shortDescription,
        String description,
        String specification,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean hasOptions,
        Double price,
        String thumbnailMediaUrl,
        List<String> productImageMediaUrls
) {
}
