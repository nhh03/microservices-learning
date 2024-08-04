package com.nhh203.productservice.repository;


import com.nhh203.productservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Page<Category> findAll(Pageable pageable);
    Page<Category> findByCategoryTitleContaining(String categoryTitle, Pageable pageable);
     @Query("select e from Category e where e.categoryTitle = ?1 and (?2 is null or e.categoryId != ?2)")
    Category findExistedName(String name, Integer id);
    Optional<Category> findBySlug(String slug);

}
