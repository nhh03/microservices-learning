package com.nhh203.productservice.viewmodel.productattribute;
import jakarta.validation.constraints.NotBlank;

public record ProductAttributePostVm(
		@NotBlank String name,
		Long productAttributeGroupId
) {
}
