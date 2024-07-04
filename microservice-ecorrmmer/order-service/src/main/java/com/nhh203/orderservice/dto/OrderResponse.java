package com.nhh203.orderservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String phoneNumber;
    private String address;
    private String statusDelivery; // Đóng hàng, Lấy hàng, giao hàng, đã giao
    private String statusOrder; // Chờ thanh toán, Vận chuyển, Chờ giao hàng, Hoàn thành, Đã hủy, Trả hàng/Hoàn tiền
    private Double totalMoney;
    private Long idSeller;
    private List<OrderLineItemResponse> orderItems;

}
