package com.nhh203.orderservice.model;

import lombok.*;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "order_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem extends AbstractAuditEntity  {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String productId;
	private String productName;
	private int quantity;
	private BigDecimal productPrice;
	private String note;
	private BigDecimal discountAmount;
	private BigDecimal taxAmount;
	private BigDecimal taxPercent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderId", referencedColumnName = "id")
	private Order orderId;

}
