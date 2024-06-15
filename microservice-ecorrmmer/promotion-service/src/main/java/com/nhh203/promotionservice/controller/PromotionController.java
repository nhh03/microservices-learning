package com.nhh203.promotionservice.controller;


import com.nhh203.promotionservice.service.IDiscount;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final IDiscount discountCode;

    @GetMapping("/test")
    public String hello(){
        return "Hello World!";
    }




}
