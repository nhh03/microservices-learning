package com.nhh203.orderservice.model;

import com.nhh203.orderservice.model.enumeration.ECheckoutState;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table(name = "checkout")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkout extends AbstractAuditEntity {
	@Id
	private String id;
	private String email;
	private String note;
	private String couponCode;

	@Enumerated(EnumType.STRING)
	private ECheckoutState checkoutState;

	@OneToMany(mappedBy = "checkoutId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	Set<CheckoutItem> checkoutItem;
}
