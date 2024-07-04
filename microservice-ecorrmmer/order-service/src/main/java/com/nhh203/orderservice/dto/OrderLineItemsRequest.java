package com.nhh203.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Double price;
    private String productId;
    private Integer quantity;
    private String note;
}

