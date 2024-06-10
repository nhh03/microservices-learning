package com.nhh203.notificationservice;

import com.nhh203.notificationservice.constant.KafkaConstant;
import com.nhh203.notificationservice.dto.EmailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }


    @KafkaListener(topics = KafkaConstant.PROFILE_ONBOARDING_TOPIC)
    public void handleNotification(EmailDetails emailDetails) {
        log.info("Got message <{}>", emailDetails.toString());
    }
}
