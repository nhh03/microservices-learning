package com.nhh203.productservice.repository;

import com.nhh203.productservice.model.attribute.ProductAttributeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeTemplateRepository extends JpaRepository<ProductAttributeTemplate,Long> {
    List<ProductAttributeTemplate> findAllByProductTemplateId(Long productTemplateId);
}
