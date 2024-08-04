package com.nhh203.orderservice.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineItemResponse {
    private String note;
    private String productId;
    private String name;
    private String img;
    private int quantity;
    private Double price;
}
