package com.nhh203.productservice.viewmodel.category;

import com.nhh203.productservice.model.Category;

public record CategoryGetDetailVm(
		long Id,
		String name,
		String slug,
		String description,
		long parentId,
		String metaKeywords,
		String metaDescription,
		short displayOrder,
		Boolean isPublish
) {
	public static CategoryGetDetailVm fromModel(Category category) {
		if (category.getParent() != null)
			return new CategoryGetDetailVm(category.getCategoryId(), category.getCategoryTitle(), category.getSlug(),
					category.getDescription(), category.getParent().getCategoryId(), category.getMetaKeyword(),
					category.getMetaDescription(), category.getDisplayOrder(), category.getIsPublished());
		else
			return new CategoryGetDetailVm(category.getCategoryId(), category.getCategoryTitle(), category.getSlug(),
					category.getDescription(), 0L, category.getMetaKeyword(), category.getMetaDescription(),
					category.getDisplayOrder(), category.getIsPublished());
	}
}
