package com.nhh203.orderservice.service;


import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.dto.OrderLineItemsRequest;
import com.nhh203.orderservice.event.OrderPlacedEvent;
import com.nhh203.orderservice.model.OrderLineItems;
import com.nhh203.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
//        Order order = new Order();
//        order.setOrderNumber(UUID.randomUUID().toString());
//
//
//        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//
//        order.setOrderLineItemsList(orderLineItems);
//
//        List<String> skuCodes = order.getOrderLineItemsList().stream()
//                .map(OrderLineItems::getSkuCode)
//                .toList();
//
//        Span inventoryServiceLookup = tracer.nextSpan().name("inventoryServiceLookup");
//
//        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
//
//            // call to  inventoryService  and place order if product is in stock
//            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
//                    .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
//                    .retrieve()
//                    .bodyToMono(InventoryResponse[].class)
//                    .block();
//
//            if (inventoryResponseArray.length > 0) {
//                boolean allProductInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
//                if (allProductInStock) {
//                    orderRepository.save(order);
//                    kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
//                    return "Order Placed";
//                } else {
//                    throw new IllegalArgumentException("Product is not in stock , please try again later ");
//                }
//            } else {
//                throw new IllegalArgumentException("Empty");
//
//            }
//        } finally {
//
//            inventoryServiceLookup.end();
//
//        }

        return "ok";

    }


    private OrderLineItems mapToDto(OrderLineItemsRequest orderLineItemsRequest) {
        OrderLineItems orderLineItems = new OrderLineItems();
//        orderLineItems.setPrice(orderLineItemsDto.getPrice());
//        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
//        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
