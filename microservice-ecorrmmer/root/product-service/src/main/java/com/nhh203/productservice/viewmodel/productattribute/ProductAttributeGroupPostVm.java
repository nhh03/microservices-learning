package com.nhh203.productservice.viewmodel.productattribute;


import com.nhh203.productservice.model.attribute.ProductAttributeGroup;
import jakarta.validation.constraints.NotBlank;
public record ProductAttributeGroupPostVm(@NotBlank String name) {

    public ProductAttributeGroup toModel(){
        ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setName(name);
        return productAttributeGroup;
    }
}
