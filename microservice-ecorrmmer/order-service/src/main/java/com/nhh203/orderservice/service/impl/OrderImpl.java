package com.nhh203.orderservice.service.impl;


import com.nhh203.orderservice.dto.OrderLineItemResponse;
import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.dto.OrderResponse;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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

        Order order = Order.builder().phoneNumber(orderRequest.getPhoneNumber()).address(orderRequest.getAddress()).statusDelivery(orderRequest.getStatusDelivery()).statusOrder(orderRequest.getStatusOrder()).totalMoney(orderRequest.getTotalMoney()).idSeller(orderRequest.getIdSeller()).build();

        List<OrderLineItems> orderItemsEntityList = orderRequest.getOrderLineItemsRequestList().stream().map(orderLineItemsDto -> {
            OrderLineItems orderLineItems = MapperOrder.mapToOrderLineItems(orderLineItemsDto);
            orderLineItems.setOrderId(order);
            return orderLineItems;
        }).toList();
        order.setOrderLineItemsList(orderItemsEntityList);
        entityManager.persist(order);
        return order;
    }

    @Transactional
    @Override
    public void updateStatusOrder(Long orderId, String statusOrder) {
        orderRepository.updateStatusOrder(orderId, statusOrder);
    }

    @Transactional
    @Override
    public void updateStatusOrder(Long orderId, String statusOrder, String token) {
        orderRepository.updateStatusOrder(orderId, statusOrder);
    }

    @Transactional
    @Override
    public void updateStatusDeliveryOrder(Long orderId, String statusOrderDelivery) {
        orderRepository.updateStatusDelivery(orderId, statusOrderDelivery);
    }

    @Override
    public List<OrderResponse> getOrderByUser(String phoneNumber, String status, String token) {

        String query = getStatusQuery(status);
        List<Order> orders = orderRepository.findByPhoneNumberAndStatusOrder(phoneNumber, query);
        List<OrderResponse> orderResponses = orders.stream()
                .map(MapperOrder::mapToOrderResponse)
                .collect(Collectors.toList());
        List<Mono<Void>> products = orders.stream()
                .flatMap(orderResponse -> orderResponse.getOrderLineItemsList().stream())
                .map(orderLineItemResponse -> getOrderLineItemResponse(orderLineItemResponse.getProductId(), token)
                        .doOnNext(orderLineItemResponse1 -> OrderLineItemResponse
                                .builder()
                                .img(orderLineItemResponse1.getImg())
                                .name(orderLineItemResponse1.getName())
                                .note(orderLineItemResponse.getNote())
                                .build())
                        .then()
                ).collect(Collectors.toList());

        Mono.when(products).block();

        return orderResponses;
    }

    private Mono<OrderLineItemResponse> getOrderLineItemResponse(String productId, String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://product-service/api/product/" + productId)
                .retrieve()
                .bodyToMono(OrderLineItemResponse.class);
    }


    @Override
    public List<OrderResponse> getOrderBySeller(Long id, String status, String token) {

        String query = getStatusQuery(status);
        List<Order> orders = orderRepository.findByIdSellerAndStatusOrder(id, query);
        List<OrderResponse> orderResponses = orders.stream()
                .map(MapperOrder::mapToOrderResponse)
                .collect(Collectors.toList());

        List<OrderLineItemResponse> allOrderLineItems = orders.stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .map(orderLineItems -> OrderLineItemResponse.builder()
                        .productId(orderLineItems.getProductId())
                        .quantity(orderLineItems.getQuantity())
                        .price(orderLineItems.getPrice())
                        .note(orderLineItems.getNote())
                        .build())
                .collect(Collectors.toList());


        Flux.fromIterable(allOrderLineItems)
                .flatMap(orderLineItemResponse -> getOrderLineItemResponse(orderLineItemResponse.getProductId(), token)
                        .doOnNext(orderLineItemResponse1 -> OrderLineItemResponse
                                .builder()
                                .img(orderLineItemResponse1.getImg())
                                .name(orderLineItemResponse1.getName())
                                .note(orderLineItemResponse.getNote())
                                .build())
                        .then()
                ).then().block();

        return orderResponses;


    }

    @Override
    public Order getOneOder(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    private String getStatusQuery(String status) {
        switch (status) {
            case "1":
                return "Chờ xác nhận";
            case "2":
                return "Chờ lấy hàng";
            case "3":
                return "Chờ giao hàng";
            case "4":
                return "Hoàn thành";
            case "5":
                return "Đã hủy";
            case "6":
                return "Trả hàng/Hoàn tiền";
            default:
                return "";
        }
    }


}
