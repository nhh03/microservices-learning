package com.nhh203.productservice.controller;


import com.nhh203.productservice.dto.req.ProductRequest;
import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
//        productService.createProduct(productRequest);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<List<ProductResponse>> getAllProducts() {
        return productService.getAllProducts();
    }


}
