package com.nhh203.orderservice.helper;

import com.nhh203.orderservice.dto.OrderLineItemResponse;
import com.nhh203.orderservice.dto.OrderLineItemsRequest;
import com.nhh203.orderservice.dto.OrderResponse;
import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.model.OrderLineItems;

import java.util.stream.Collectors;

public class MapperOrder {
    public static OrderLineItems mapToOrderLineItems(OrderLineItemsRequest orderLineItemsRequest) {
        return OrderLineItems.builder()
                .note(orderLineItemsRequest.getNote())
                .productId(orderLineItemsRequest.getProductId())
                .quantity(orderLineItemsRequest.getQuantity())
                .price(orderLineItemsRequest.getPrice())
                .build();
    }

    public static OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .statusDelivery(order.getStatusDelivery())
                .statusOrder(order.getStatusOrder())
                .totalMoney(order.getTotalMoney())
                .idSeller(order.getIdSeller())
                .orderItems(order.getOrderLineItemsList().stream()
                        .map(orderLineItems -> OrderLineItemResponse.builder()
                                .productId(orderLineItems.getProductId())
                                .quantity(orderLineItems.getQuantity())
                                .price(orderLineItems.getPrice())
                                .note(orderLineItems.getNote())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
