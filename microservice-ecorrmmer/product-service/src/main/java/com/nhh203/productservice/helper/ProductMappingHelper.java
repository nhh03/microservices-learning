package com.nhh203.productservice.helper;


import com.nhh203.productservice.dto.res.CategoryResponse;
import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.model.Product;

public interface ProductMappingHelper {
    static ProductResponse mapToProductResponse(final Product product) {
        return ProductResponse.builder()
                .id(product.getProductId())
                .productTitle(product.getProductTitle())
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .categoryResponse(
                        CategoryResponse
                                .builder()
                                .categoryId(product.getCategory().getCategoryId())
                                .categoryTitle(product.getCategory().getCategoryTitle())
                                .imageUrl(product.getCategory().getImageUrl())
                                .createAt(String.valueOf(product.getCategory().getCreateAt()))
                                .updateAt(String.valueOf(product.getCategory().getUpdateAt()))
                                .build())
                .createAt(String.valueOf(product.getCreateAt()))
                .updateAt(String.valueOf(product.getUpdateAt()))
                .build();
    }

//    static Product maptoProduct(final ProductRequest productRequest){
//
//    }

}
