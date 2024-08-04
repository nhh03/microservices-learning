package com.nhh203.productservice.helper;

import com.nhh203.productservice.dto.res.CategoryResponse;
import com.nhh203.productservice.model.Category;

public interface CategoryMappingHelper {

    static CategoryResponse mapToCategoryResponse(final Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryTitle(category.getCategoryTitle())
                .imageUrl(category.getImageUrl())
                .createAt(String.valueOf(category.getCreateAt()))
                .updateAt(String.valueOf(category.getUpdateAt()))
                .build();
    }

}
