package com.nhh203.orderservice.config;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class GsonConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
