package com.nhh203.cartservice;

import com.nhh203.cartservice.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class CartServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CartServiceApplication.class, args);
	}

}
