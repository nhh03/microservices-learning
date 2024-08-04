package com.nhh203.productservice.repository;


import com.nhh203.productservice.model.Category;
import com.nhh203.productservice.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
	List<ProductCategory> findAllByProductProductId(String productId);

	Page<ProductCategory> findAllByCategory(Pageable pageable, Category category);
}
