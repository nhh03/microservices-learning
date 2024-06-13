package com.nhh203.productservice.service;


import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.dto.req.ProductRequest;
import reactor.core.publisher.Flux;

import java.util.List;


public interface ProductService {

    Flux<ProductResponse> getAllProducts();

    ProductResponse findById(final String productId);

    ProductResponse save(final ProductRequest productDto);

    ProductResponse update(final ProductRequest productDto);

    ProductResponse update(final String productId, final ProductRequest productDto);

    void deleteById(final String productId);


}

