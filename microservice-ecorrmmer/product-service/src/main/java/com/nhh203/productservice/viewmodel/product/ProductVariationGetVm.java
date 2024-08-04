package com.nhh203.productservice.viewmodel.product;

import com.nhh203.productservice.viewmodel.ImageVm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductVariationGetVm(String id,
                                    String name,
                                    String slug,
                                    String sku,
                                    String gtin,
                                    BigDecimal price,
                                    ImageVm thumbnail,
                                    List<ImageVm> productImages,
                                    Map<String, String> optionss){
}
