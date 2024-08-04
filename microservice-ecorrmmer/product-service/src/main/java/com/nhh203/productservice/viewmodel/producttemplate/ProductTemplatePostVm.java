package com.nhh203.productservice.viewmodel.producttemplate;

import com.nhh203.productservice.viewmodel.productattribute.ProductAttributeTemplatePostVm;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ProductTemplatePostVm(@NotBlank String name,
                                    List<ProductAttributeTemplatePostVm> ProductAttributeTemplates) {

}
