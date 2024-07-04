package com.nhh203.orderservice.service;

import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.dto.OrderResponse;
import com.nhh203.orderservice.model.Order;

import java.util.List;

public interface IOrder {
    Order createOder(OrderRequest orderRequest);

    void updateStatusOrder(Long orderId, String statusOrder);

    void updateStatusOrder(Long orderId, String statusOrder, String token);

    void updateStatusDeliveryOrder(Long orderId, String statusOrderDelivery);

    List<OrderResponse> getOrderByUser(String phoneNumber, String status, String token);

    List<OrderResponse> getOrderBySeller(Long id, String status, String token);

    Order getOneOder(Long orderId);
}