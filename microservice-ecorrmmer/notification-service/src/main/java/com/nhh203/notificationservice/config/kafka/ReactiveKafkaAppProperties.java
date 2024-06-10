package com.nhh203.notificationservice.config.kafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveKafkaAppProperties {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    String consumerGroupId;
}