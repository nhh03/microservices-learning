package com.nhh203.orderservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nhh203.orderservice.model.enumeration.EDeliveryMethod;
import com.nhh203.orderservice.model.enumeration.EDeliveryStatus;
import com.nhh203.orderservice.model.enumeration.EOrderStatus;
import com.nhh203.orderservice.model.enumeration.EPaymentStatus;
import lombok.*;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AbstractAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "shipping_address_id", referencedColumnName = "id")
	private OrderAddress shippingAddressId;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_address_id", referencedColumnName = "id")
	private OrderAddress billingAddressId;

	private String note;
	private float tax;
	private float discount;
	private int numberItem;
	private String couponCode;
	private BigDecimal totalPrice;
	private BigDecimal deliveryFee;
	private String phoneNumber;
	private Long idSeller;

	@Enumerated(EnumType.STRING)
	private EOrderStatus orderStatus;

	@Enumerated(EnumType.STRING)
	private EDeliveryMethod deliveryMethod;

	@Enumerated(EnumType.STRING)
	private EDeliveryStatus deliveryStatus;

	@Enumerated(EnumType.STRING)
	private EPaymentStatus paymentStatus;
	private Long paymentId;

	@OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<OrderItem> orderItems;
	private String checkoutId;
	private String rejectReason;

}
