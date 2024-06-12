package com.nhh203.productservice.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer categoryId;
    private String categoryTitle;
    private String imageUrl;
    private String createAt;
    private String updateAt;
}
