package com.nhh203.productservice.viewmodel.product;


import com.nhh203.productservice.model.Product;

public record ProductInfoVm(String id, String name, String sku) {
    public static ProductInfoVm fromProduct(Product product) {
        return new ProductInfoVm(product.getProductId(), product.getProductTitle(), product.getSku());
    }
}
