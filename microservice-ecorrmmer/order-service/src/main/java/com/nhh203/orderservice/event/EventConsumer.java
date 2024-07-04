package com.nhh203.orderservice.event;


import com.google.gson.Gson;
import com.nhh203.orderservice.service.impl.OrderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventConsumer {
    private final Gson gson;
    private final OrderImpl order;

//    @KafkaListener(topics = "${kafka.topic.orderCancel}", groupId = "${kafka.consumer.groupId}")
//    public void consumeOrderCancel(String message) {
//        log.info("order-service: Consumed message: " + message);
//        try {
//            Long idOrder = Long.parseLong(message);
//            order.updateStatusOrder(idOrder, "Đã hủy");
//        } catch (NumberFormatException e) {
//            log.error("Failed to parse order ID from message: {}", message, e);
//        }
//    }

}
