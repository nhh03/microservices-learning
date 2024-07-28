package com.nhh203.cartservice.model.dto.response;

import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseWithMessage {
    private String message;
    private List<CartResponse> data;
}
