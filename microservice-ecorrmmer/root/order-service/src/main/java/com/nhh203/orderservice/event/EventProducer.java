package com.nhh203.orderservice.event;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventProducer {
    private final KafkaTemplate<String, String> template;
    private final ObjectMapper objectMapper; // ObjectMapper for JSON serialization

    public void sendMessage(String topic, String message) {
        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, message); // Create a ProducerRecord
        CompletableFuture<SendResult<String, String>> future = template.send(record);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("order-service: Message sent successfully: " + result.getRecordMetadata().offset());
            } else {
                log.error("order-service: Error sending message: ", ex);
            }
        });
    }

    public void sendMessage(String topic, Object data) {
        try {
            String value = objectMapper.writeValueAsString(data);
            sendMessage(topic, value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
