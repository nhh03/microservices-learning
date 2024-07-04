package com.nhh203.productservice.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ProductResponse {
    private String id;
    private String productTitle;
    private BigDecimal price;
    private String imageUrl;
    private String sku;
    private Integer quantity;
    private CategoryResponse categoryResponse;
    private String createAt;
    private String updateAt;
}