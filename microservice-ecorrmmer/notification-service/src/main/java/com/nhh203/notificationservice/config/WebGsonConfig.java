package com.nhh203.notificationservice.config;


import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebGsonConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
