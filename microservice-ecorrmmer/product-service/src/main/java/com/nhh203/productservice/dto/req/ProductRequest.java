package com.nhh203.productservice.dto.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhh203.productservice.model.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private String name;
    private String productTitle;
    private String imageUrl;
    private String sku;
    private BigDecimal price;
    private Integer quantity;
    @JsonProperty("category_id")
    private Long categoryId;
}
