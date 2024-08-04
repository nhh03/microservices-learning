package com.nhh203.orderservice.viewmodel.product;


import lombok.Builder;

@Builder
public record ProductQuantityItem(String productId, Long quantity) {
}
