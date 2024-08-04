package com.nhh203.productservice.repository;


import com.nhh203.productservice.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

	@Query("select e from ProductOption e where e.name = ?1 and (?2 is null or e.id != ?2)")
	ProductOption findExistedName(String name, Long id);
}
