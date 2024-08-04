package com.nhh203.cartservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cart_item")
public class CartItemEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String productId;

	private String parentProductId;

	private int quantity;


	private String color;

	private String size;

	@ManyToOne
	@JoinColumn(name = "cart_id", nullable = false)
	@JsonIgnore
	private CartEntity cart;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CartItemEntity)) {
			return false;
		}
		return id != null && id.equals(((CartItemEntity) o).id);
	}

	@Override
	public int hashCode() {
		// see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return getClass().hashCode();
	}

}
