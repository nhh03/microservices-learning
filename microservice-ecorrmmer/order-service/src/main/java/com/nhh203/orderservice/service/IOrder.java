package com.nhh203.orderservice.service;

import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.model.Order;

public interface IOrder {
     Order createOder(OrderRequest orderRequest);
}
