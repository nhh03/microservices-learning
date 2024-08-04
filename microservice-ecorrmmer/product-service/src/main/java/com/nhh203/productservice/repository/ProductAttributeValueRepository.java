package com.nhh203.productservice.repository;

import com.nhh203.productservice.model.Product;
import com.nhh203.productservice.model.attribute.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    List<ProductAttributeValue> findAllByProduct(Product product);
}
