package com.nhh203.productservice.repository;


import com.nhh203.productservice.model.Product;
import com.nhh203.productservice.model.ProductRelated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRelatedRepository extends JpaRepository<ProductRelated, Long> {
	    Page<ProductRelated> findAllByProduct(Product product, Pageable pageable);

}
