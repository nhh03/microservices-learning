package com.nhh203.productservice.viewmodel.productattribute;

import java.util.List;

public record ProductAttributeGroupGetVm(String name, List<ProductAttributeValueVm> productAttributeValues) {
}
