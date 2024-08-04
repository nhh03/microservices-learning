package com.nhh203.productservice.repository;


import com.nhh203.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ProductImageRepository extends JpaRepository<com.nhh203.productservice.model.ProductImage, Long> {
	void deleteByImageIdInAndProduct(List<Long> imageIds, Product product);
}
