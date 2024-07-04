package com.nhh203.orderservice.controller;


import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.event.EventProducer;
import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.service.IOrder;
import com.nhh203.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final IOrder orderService;
    private final EventProducer eventProducer;

    @GetMapping("/demo")
    public String demo() {
        return "Hello World";
    }

    @GetMapping("/user/{phoneNumber}/{status}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable String phoneNumber, @PathVariable String status, @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByUser(phoneNumber, status, token));
    }

    @GetMapping("/seller/{sellerId}/{status}")
    public ResponseEntity<?> getOrdersBySeller(@PathVariable String sellerId, @PathVariable String status, @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderBySeller(Long.valueOf(sellerId), status, token));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOneOder(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("{statuspayment}")
    public ResponseEntity<?> createOrder(@PathVariable("statuspayment") String statuspayment, @RequestBody OrderRequest orderRequest) {
        Order createdOrder = orderService.createOder(orderRequest);
        if (createdOrder != null) {
            if (statuspayment.equals("0")) {
                String content = createdOrder.getId().toString();
                eventProducer.sendMessage("payment-cod", content);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order not created");
        }

    }


    @PutMapping("/update/{orderId}/status")
    public ResponseEntity<Void> updateStatusOrder(@PathVariable Long orderId, @RequestParam String statusOrder, @RequestParam(defaultValue = "0") String token) {
        if (token.equals("0")) {
            orderService.updateStatusOrder(orderId, statusOrder);
        } else {
            orderService.updateStatusOrder(orderId, statusOrder, token);
        }
        return ResponseEntity.ok().build();
    }


    @PutMapping("/update/{orderId}/delivery")
    public ResponseEntity<Void> updateStatusDeliveryOrder(@PathVariable Long orderId, @RequestParam String statusDelivery) {
        orderService.updateStatusDeliveryOrder(orderId, statusDelivery);
        return ResponseEntity.ok().build();
    }


//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
////    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
////    @TimeLimiter(name = "inventory")
////    @Retry(name = "inventory")
//    public String placeOrder(@RequestBody OrderRequest orderRequest) {
//        log.info("Placing Order");
////        return CompletableFuture.supplyAsync(() -> );
//        return orderService.placeOrder(orderRequest);
//    }
//
//
//    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException exception) {
//        log.info("Cannot Place Order Executing Fallback logic");
//        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time!");
//
//    }
}
