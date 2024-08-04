package com.nhh203.productservice.viewmodel.product;

import com.nhh203.productservice.validation.ValidateProductPrice;
import jakarta.validation.constraints.NotBlank;

import java.util.List;


public record ProductPostVm(@NotBlank
                            String name,
                            @NotBlank
                            String slug,
                            Long brandId,
                            List<Integer> categoryIds,
                            String shortDescription,
                            String description,
                            String specification,
                            String sku,
                            String gtin,
                            @ValidateProductPrice
                            Double price,
                            Boolean isAllowedToOrder,
                            Boolean isPublished,
                            Boolean isFeatured,
                            Boolean isVisibleIndividually,
                            Boolean stockTrackingEnabled,

                            Boolean taxIncluded,
                            String metaTitle,
                            String metaKeyword,
                            String metaDescription,
                            Long thumbnailMediaId,
                            List<Long> productImageIds,
                            List<ProductVariationPostVm> variations,
                            List<ProductOptionValuePostVm> productOptionValues,
                            List<String> relatedProductIds,
                            Long taxClassId) {

}
