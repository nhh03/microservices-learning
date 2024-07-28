package com.nhh203.productservice.repository;


import com.nhh203.productservice.model.Product;
import com.nhh203.productservice.model.ProductOptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionCombinationRepository extends JpaRepository<ProductOptionCombination, Long> {
	List<ProductOptionCombination> findAllByProduct(Product product);
}
