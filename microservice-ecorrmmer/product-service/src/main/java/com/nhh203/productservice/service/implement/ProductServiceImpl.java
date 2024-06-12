package com.nhh203.productservice.service.implement;

import com.google.gson.Gson;
import com.nhh203.productservice.Utils.BeanUtilsUpdate;
import com.nhh203.productservice.dto.req.ProductRequest;
import com.nhh203.productservice.dto.res.ProductResponse;
import com.nhh203.productservice.exception.wrapper.CategoryNotFoundException;
import com.nhh203.productservice.exception.wrapper.ProductNotFoundException;
import com.nhh203.productservice.helper.ProductMappingHelper;
import com.nhh203.productservice.model.Category;
import com.nhh203.productservice.model.Product;
import com.nhh203.productservice.repository.CategoryRepository;
import com.nhh203.productservice.repository.ProductRepository;
import com.nhh203.productservice.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;
    Gson gson;

    @Override
    public Flux<ProductResponse> getAllProducts() {
        log.info("ProductDto List, service, fetch all products");
        return Flux.fromIterable(this.productRepository.findAll()).map(ProductMappingHelper::mapToProductResponse).distinct().onErrorResume(throwable -> {
            log.error("Error while fetching product: " + throwable.getMessage());
            return Flux.empty();
        });
    }

    @Override
    public ProductResponse findById(Integer productId) {
        log.info("ProductDto, service; fetch product by id");
        Optional<Product> product = this.productRepository.findById(String.valueOf(productId));
        if (product.isEmpty()) {
            log.error("Product with id {} not found", productId);
            return null;
        }
        return ProductMappingHelper.mapToProductResponse(product.get());
    }

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        log.info("ProductDto, service; save product");

        Optional<Category> category = this.categoryRepository.findById(productRequest.getCategoryId());

        if (category.isEmpty()) {
            log.error("Category with id {} not found", productRequest.getCategoryId());
            throw new CategoryNotFoundException("Not found category id : productRequest.getCategoryId())");
        }

        Product product = modelMapper.map(productRequest, Product.class);
        product.setCategory(category.get());
        this.productRepository.save(product);
        return ProductMappingHelper.mapToProductResponse(product);

    }

    @Override
    public ProductResponse update(ProductRequest productDto) {
        return null;
    }

    @Override
    public ProductResponse update(String productId, ProductRequest productRequest) {
        log.info("productRequest, service; update product by id");
        try {
            Product existingProduct = this.productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
            if (productRequest.getCategoryId() != null) {
                Optional<Category> category = this.categoryRepository.findById(productRequest.getCategoryId());
                if (category.isEmpty()) {
                    log.error("Category with id {} not found", productRequest.getCategoryId());
                    throw new CategoryNotFoundException("Not found category id : productRequest.getCategoryId())");
                }
                existingProduct.setCategory(category.get());
            }
            BeanUtilsUpdate.copyNonNullProperties(productRequest, existingProduct);
            this.productRepository.save(existingProduct);
            return ProductMappingHelper.mapToProductResponse(existingProduct);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ProductNotFoundException("Error update product", exception);
        }
    }

    @Override
    public void deleteById(String productId) {
        log.info("Void, service; delete product by id");
        try {
            Optional<Product> product = this.productRepository.findById(productId);
            if (product.isEmpty()) {
                log.error("Product with id {} not found", productId);
                throw new ProductNotFoundException("Not found product id :" + productId);
            }
            this.productRepository.deleteById(String.valueOf(productId));

        } catch (Exception e) {
            throw new ProductNotFoundException("Error delete product", e);
        }
    }
}
