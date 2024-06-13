package com.nhh203.orderservice.helper;

import com.nhh203.orderservice.dto.OrderLineItemsDto;
import com.nhh203.orderservice.model.OrderLineItems;

public class MapperOrder {
    public static OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .note(orderLineItemsDto.getNote())
                .productId(orderLineItemsDto.getProductId())
                .quantity(orderLineItemsDto.getQuantity())
                .price(orderLineItemsDto.getPrice())
                .build();
    }
}
