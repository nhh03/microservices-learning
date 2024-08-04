package com.nhh203.productservice.repository;

import com.nhh203.productservice.model.Brand;
import com.nhh203.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
	Optional<Product> findBySlugAndIsPublishedTrue(String slug);

	@Query(value = "select p from Product p where LOWER(p.productTitle) LIKE %:productName% " +
			"and (p.brand.name in :brandName or (:brandName is NULL  or :brandName = '')) " +
			"and p.isVisibleIndividually = TRUE " +
			"and p.isPublished = TRUE " +
			"order by p.updateAt desc"
	)
	Page<Product> getProductsWithFilter(@Param("productName") String productName, @Param("brandName") String brandName, org.springframework.data.domain.Pageable pageable);

	List<Product> findAllByProductIdIn(List<String> productIds);

	List<Product> findAllByBrandAndIsPublishedTrue(Brand brand);

	@Query(value = "FROM Product p WHERE p.isFeatured = TRUE " +
			"AND p.isVisibleIndividually = TRUE " +
			"AND p.isPublished = TRUE ORDER BY p.updateAt DESC")
	Page<Product> getFeaturedProduct(Pageable pageable);

	@Query(value = "SELECT p FROM Product p LEFT JOIN p.productCategories pc LEFT JOIN pc.category c " +
			"WHERE LOWER(p.productTitle) LIKE %:productName% " +
			"AND (c.slug = :categorySlug OR (:categorySlug IS NULL OR :categorySlug = '')) " +
			"AND (:startPrice IS NULL OR p.price >= :startPrice) " +
			"AND (:endPrice IS NULL OR p.price <= :endPrice) " +
			"AND p.isVisibleIndividually = TRUE " +
			"AND p.isPublished = TRUE " +
			"ORDER BY p.updateAt DESC")
	Page<Product> findByProductNameAndCategorySlugAndPriceBetween(@Param("productName") String productName,
	                                                              @Param("categorySlug") String categorySlug,
	                                                              @Param("startPrice") Double startPrice,
	                                                              @Param("endPrice") Double endPrice,
	                                                              Pageable pageable);


	@Query(value = "SELECT p FROM Product p " +
			"WHERE (LOWER(p.productTitle) LIKE concat('%', LOWER(:name), '%') " +
			"OR LOWER(p.sku) LIKE concat('%', LOWER(:sku), '%')) " +
			"AND (:selection = 'ALL' " +
			"OR (:selection = 'YES' AND p.productId IN :productIds) " +
			"OR (:selection = 'NO' AND (COALESCE(:productIds, NULL) IS NULL OR p.productId NOT IN :productIds)))")
	List<Product> findProductForWarehouse(@Param("name") String name, @Param("sku") String sku,
	                                      @Param("productIds") List<Long> productIds,
	                                      @Param("selection") String selection);

}