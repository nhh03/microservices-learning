package com.nhh203.cartservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {
    private String customerId;
    private String productId;
    private int quantity;
    private String color;
    private String size;
}