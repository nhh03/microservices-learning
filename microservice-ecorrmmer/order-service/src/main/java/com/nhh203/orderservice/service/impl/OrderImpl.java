package com.nhh203.orderservice.service.impl;


import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.helper.MapperOrder;
import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.model.OrderLineItems;
import com.nhh203.orderservice.repository.OrderRepository;
import com.nhh203.orderservice.service.IOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderImpl implements IOrder {
    OrderRepository orderRepository;
    WebClient.Builder webClientBuilder;

    @PersistenceContext
    private EntityManager entityManager;



    @Transactional
    @Override
    public Order createOder(OrderRequest orderRequest) {

        Order order = Order.builder()
                .phoneNumber(orderRequest.getPhoneNumber())
                .address(orderRequest.getAddress())
                .statusDelivery(orderRequest.getStatusDelivery())
                .statusOrder(orderRequest.getStatusOrder())
                .totalMoney(orderRequest.getTotalMoney())
                .idSeller(orderRequest.getIdSeller())
                .build();

        List<OrderLineItems> orderItemsEntityList = orderRequest
                .getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> {
                    OrderLineItems orderLineItems = MapperOrder.mapToOrderLineItems(orderLineItemsDto);
                    orderLineItems.setOrderId(order);
                    return orderLineItems;
                })
                .toList();
        order.setOrderLineItemsList(orderItemsEntityList);
        entityManager.persist(order);
        return order;
    }
}
