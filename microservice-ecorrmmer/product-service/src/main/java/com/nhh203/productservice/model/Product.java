package com.nhh203.productservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nhh203.productservice.model.attribute.ProductAttribute;
import com.nhh203.productservice.model.attribute.ProductAttributeValue;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "products")
public class Product extends AbstractMappedEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "product_id", unique = true, nullable = false, updatable = false)
	private String productId;

	@Column(name = "product_title")
	private String productTitle;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(unique = true)
	private String sku;

	@Column(name = "price", columnDefinition = "decimal")
	private BigDecimal price;

	@Column(name = "quantity")
	private Integer quantity;

	private String shortDescription;

	private String description;

	private String specification;

	private String gtin;

	private String slug;
	private boolean hasOptions;

	private boolean isAllowedToOrder;

	private boolean isPublished;

	private boolean isFeatured;

	private boolean isVisibleIndividually;

	private boolean stockTrackingEnabled;

	private Long stockQuantity;

	private Long taxClassId;

	private String metaTitle;

	private String metaKeyword;

	private String metaDescription;

	private Long thumbnailMediaId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	@OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST})
	@Builder.Default
	private List<ProductImage> productImages = new ArrayList<ProductImage>();

	@OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST})
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();

	@OneToMany(mappedBy = "product")
	@Builder.Default
	List<ProductRelated> relatedProducts = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Product parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@JsonIgnore
	@Builder.Default
	private List<Product> products = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	@Builder.Default
	private List<ProductAttributeValue> attributeValues = new ArrayList<ProductAttributeValue>();

	private boolean taxIncluded;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Product)) {
			return false;
		}
		return productId != null && productId.equals(((Product) o).productId);
	}

	@Override
	public int hashCode() {
		// see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return getClass().hashCode();
	}


}