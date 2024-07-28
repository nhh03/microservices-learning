package com.nhh203.productservice.viewmodel.productattribute;

import com.nhh203.productservice.model.attribute.ProductAttribute;

public record ProductAttributeVm(Long id, String name) {
    public static ProductAttributeVm fromModel(ProductAttribute productAttribute){
        return new ProductAttributeVm(productAttribute.getId(),productAttribute.getName());
    }
}