package com.nhh203.productservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true, exclude = {"subCategories", "parentCategory", "products"})
//@Data
@Getter
@Setter
@Builder
@Entity
@Table(name = "categories")
public class Category extends AbstractMappedEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id", unique = true, nullable = false, updatable = false)
	private Integer categoryId;

	@Column(name = "category_title")
	private String categoryTitle;

	@Column(name = "image_url")
	private String imageUrl;

	private String description;

    private String slug;

    private String metaKeyword;

    private String metaDescription;

    private Short displayOrder;

    private Boolean isPublished;

    private Long imageId;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Category> categories = new ArrayList<>();

	@OneToMany(mappedBy = "category")
	@JsonIgnore
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Category)) {
			return false;
		}
		return categoryId != null && categoryId.equals(((Category) o).categoryId);
	}

	@Override
	public int hashCode() {
		// see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return getClass().hashCode();
	}


}
