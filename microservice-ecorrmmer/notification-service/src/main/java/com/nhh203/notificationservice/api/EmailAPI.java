package com.nhh203.notificationservice.api;


import com.nhh203.notificationservice.dto.EmailDetails;
import com.nhh203.notificationservice.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/email")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EmailAPI {
    EmailService emailService;

    @PostMapping("/sendSimpleMail")
    public Mono<String> sendSimpleMail(@RequestBody EmailDetails details) {
        return emailService.sendSimpleMail(details);
    }

    @PostMapping("/sendMailWithAttachment")
    public Mono<String> sendMailWithAttachment(@RequestBody EmailDetails details) {
        return emailService.sendMailWithAttachment(details);
    }

    @PostMapping("/sendMail")
    public Mono<String> sendMail(@RequestParam(value = "file", required = false) MultipartFile[] files,
                                 @RequestParam String to,
                                 @RequestParam String[] cc,
                                 @RequestParam String subject,
                                 @RequestParam String body) {
        return emailService.sendMail(files, to, cc, subject, body);
    }

}
