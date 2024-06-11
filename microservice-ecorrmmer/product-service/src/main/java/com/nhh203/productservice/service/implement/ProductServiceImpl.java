package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.dto.req.ProductRequest;
import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.helper.ProductMappingHelper;
import com.nhh203.productservice.model.Product;
import com.nhh203.productservice.repository.ProductRepository;
import com.nhh203.productservice.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;

    @Override
    public Flux<List<ProductResponse>> getAllProducts() {
        log.info("ProductDto List, service, fetch all products");
        return Flux.defer(() -> {
            List<ProductResponse> products = this.productRepository
                    .findAll()
                    .stream()
                    .map(ProductMappingHelper::mapToProductResponse)
                    .distinct()
                    .toList();
            return Flux.just(products);
        }).onErrorResume(throwable -> {
            log.error("Error occurred while fetching all products", throwable);
            return Flux.empty();
        });
    }

    @Override
    public ProductResponse findById(Integer productId) {
        return null;
    }

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        log.info("ProductDto, service; save product");
        Product product = Product.builder()
                .productTitle(productRequest.getName())
                .imageUrl(productRequest.getImageUrl())
                .sku(productRequest.getSku())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getProductId());
        return null;

    }

    @Override
    public ProductResponse update(ProductRequest productDto) {
        return null;
    }

    @Override
    public ProductResponse update(Integer productId, ProductRequest productDto) {
        return null;
    }

    @Override
    public void deleteById(Integer productId) {

    }
}
