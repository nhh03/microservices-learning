package com.nhh203.productservice.viewmodel.producttemplate;
import com.nhh203.productservice.model.attribute.ProductTemplate;

public record ProductTemplateGetVm(Long id, String name) {

    public static ProductTemplateGetVm fromModel(ProductTemplate productTemplate){
        return new ProductTemplateGetVm(
                productTemplate.getId(),
                productTemplate.getName()
        );
    }
}
