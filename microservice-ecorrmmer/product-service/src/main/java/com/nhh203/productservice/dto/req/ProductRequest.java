package com.nhh203.productservice.dto.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String productTitle;
    private String imageUrl;
    private String sku;
    private BigDecimal price;
    private Integer quantity;
    @JsonProperty("category_id")
    private Integer categoryId;
}
