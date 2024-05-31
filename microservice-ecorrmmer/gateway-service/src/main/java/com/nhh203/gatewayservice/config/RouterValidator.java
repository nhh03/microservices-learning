package com.nhh203.gatewayservice.config;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/users/register",
            "/api/v1/users/authenticate",
            "/api/v1/users/refresh-token",
            "/api/v1/payments/**",
            "/api/v1/users/logout",
            "/api/v1/products/**",
            "/api/v1/products/categories"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
