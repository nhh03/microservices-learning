package com.nhh203.productservice.viewmodel.category;

import com.nhh203.productservice.model.Category;
import com.nhh203.productservice.viewmodel.ImageVm;

public record CategoryGetVm(int id, String name, String slug, long parentId, ImageVm categoryImage) {
	public static CategoryGetVm fromModel(Category category) {
		Category parent = category.getParent();
		long parentId = parent == null ? -1 : parent.getCategoryId();
		return new CategoryGetVm(category.getCategoryId(), category.getCategoryTitle(), category.getSlug(), parentId, null);
	}
}
