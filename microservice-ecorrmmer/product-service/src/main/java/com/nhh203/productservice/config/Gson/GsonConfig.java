package com.nhh203.productservice.config.Gson;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {
      @Bean
    public Gson gson() {
        return new Gson();
    }
}
