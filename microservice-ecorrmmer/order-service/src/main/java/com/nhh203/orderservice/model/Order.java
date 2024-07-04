package com.nhh203.orderservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "t_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNumber;
    private String address;
    private String statusDelivery; // Đóng hàng, Lấy hàng, giao hàng, đã giao
    private String statusOrder; // Chờ xác nhận, Chờ lấy hàng, Chờ giao hàng, Hoàn thành, Đã hủy, Trả hàng/Hoàn tiền
    private Double totalMoney;
    private Long idSeller;

    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderLineItems> orderLineItemsList;
}
