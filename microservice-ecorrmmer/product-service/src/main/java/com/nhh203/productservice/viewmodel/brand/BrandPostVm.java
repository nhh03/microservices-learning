package com.nhh203.productservice.viewmodel.brand;

import com.nhh203.productservice.model.Brand;
import jakarta.validation.constraints.NotBlank;

public record BrandPostVm(Long id, @NotBlank String name, @NotBlank String slug, boolean isPublish) {
	public Brand toModel() {
		Brand brand = new Brand();
		brand.setName(name);
		brand.setSlug(slug);
		brand.setPublished(isPublish);
		return brand;
	}
}
