package com.nhh203.notificationservice.event;


import com.google.gson.Gson;
import com.nhh203.notificationservice.constant.KafkaConstant;
import com.nhh203.notificationservice.dto.EmailDetails;
import com.nhh203.notificationservice.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventConsumer {
    Gson gson;
    EmailService emailService;

    @KafkaListener(topics = KafkaConstant.PROFILE_ONBOARDING_TOPIC, groupId = "notification-groupId")
    public void sendEmailKafkaOnboarding(String message) {
        log.info("USER-SERVICE Onboarding event send email on notification service.");
        EmailDetails emailDetails = gson.fromJson(message, EmailDetails.class);

        emailService.sendSimpleMail(emailDetails).subscribe(email -> {
            log.info("Email sent successfully for onboarding.");
        }, error -> log.error("Failed to send email for onboarding: {}", error.getMessage()));
    }




}
