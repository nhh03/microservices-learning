package com.nhh203.productservice.controller;


import com.nhh203.productservice.dto.req.ProductRequest;
import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;


    // TODO : create product
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.save(productRequest);
        if (productResponse != null) {
            return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Thất bại", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // TODO : get all products
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    //TODO : get product by id
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findById(@PathVariable("productId")
                                                    @NotBlank(message = "Input must not be blank!")
                                                    @Valid final String productId) {
        log.info("ProductDto, resource; fetch product by id");
        return ResponseEntity.ok(productService.findById(productId));
    }

    // TODO: Update information of all product
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(@PathVariable("productId")
                                                  @NotBlank(message = "Input must not be blank!")
                                                  @Valid final String productId,
                                                  @RequestBody
                                                  @NotNull(message = "Input must not be NULL!")
                                                  @Valid final ProductRequest productRequest) {
        log.info("productRequest, resource; update product");
        return ResponseEntity.ok(productService.update(productId, productRequest));
    }


    //TODO: Delete a product
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteById(@PathVariable("productId") final String productId) {
        log.info("Boolean, resource; delete product by id");
        productService.deleteById(productId);
        return new ResponseEntity<>("Xóa thành công", HttpStatus.OK);
    }

}
