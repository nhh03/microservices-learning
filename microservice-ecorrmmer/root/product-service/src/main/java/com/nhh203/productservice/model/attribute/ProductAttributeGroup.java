package com.nhh203.productservice.model.attribute;

import com.nhh203.productservice.model.AbstractMappedEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_attribute_group")
@Getter
@Setter
public class ProductAttributeGroup extends AbstractMappedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProductAttributeGroup)) {
			return false;
		}
		return id != null && id.equals(((ProductAttributeGroup) o).id);
	}
	@Override
	public int hashCode() {
		// see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return getClass().hashCode();
	}
}
