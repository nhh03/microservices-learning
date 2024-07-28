package com.nhh203.productservice.viewmodel.product;

import java.util.List;

public record ProductFeatureGetVm(List<ProductThumbnailGetVm> productList, int totalPage) {
}
