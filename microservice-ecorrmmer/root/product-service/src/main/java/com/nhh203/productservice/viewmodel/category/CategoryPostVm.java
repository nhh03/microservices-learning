package com.nhh203.productservice.viewmodel.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryPostVm(@NotBlank String name,
                             @NotBlank String slug,
                             String description,
                             Integer parentId,
                             String metaKeywords,
                             String metaDescription,
                             Short displayOrder,
                             Boolean isPublish,
                             Long imageId) {
}
