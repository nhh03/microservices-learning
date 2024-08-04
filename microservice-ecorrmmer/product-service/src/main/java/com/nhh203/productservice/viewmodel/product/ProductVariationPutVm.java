package com.nhh203.productservice.viewmodel.product;

import java.util.List;

public record ProductVariationPutVm(Long id,
                                    String name,
                                    String slug,
                                    String sku,
                                    String gtin,
                                    Double price,
                                    Long thumbnailMediaId,
                                    List<Long> productImageIds) {
}
