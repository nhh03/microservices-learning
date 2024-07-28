package com.nhh203.productservice.viewmodel.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductESDetailVm(String id,
                                String name,
                                String slug,
                                BigDecimal price,
                                boolean isPublished,
                                boolean isVisibleIndividually,
                                boolean isAllowedToOrder,
                                boolean isFeatured,
                                Long thumbnailMediaId,
                                String brand,
                                List<String> categories,
                                List<String> attributes){
}
