package com.nhh203.cartservice.model.dto.response;

//import com.nhh203.commonservice.dto.ProductResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private String _id;
    private int buy_count;
    private double price;
    private double price_before_discount;
    private ProductResponse product;
    private int status;
    private String user;
    private String color;
    private String size;
}
