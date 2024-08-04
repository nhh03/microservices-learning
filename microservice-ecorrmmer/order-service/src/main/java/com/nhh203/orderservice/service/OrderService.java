package com.nhh203.orderservice.service;

import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.dto.OrderResponse;
import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.model.enumeration.EOrderStatus;
import com.nhh203.orderservice.viewmodel.order.*;

import java.time.ZonedDateTime;
import java.util.List;

public interface OrderService {
	Order createOder(OrderRequest orderRequest);

	OrderVm createOrder(OrderPostVm orderPostVm);

	OrderVm getOrderWithItemsById(long id);

	OrderListVm getAllOrder(ZonedDateTime createdFrom,
	                        ZonedDateTime createdTo,
	                        String warehouse,
	                        String productName,
	                        List<EOrderStatus> orderStatus,
	                        String billingCountry,
	                        String billingPhoneNumber,
	                        String email,
	                        int pageNo,
	                        int pageSize);

	OrderExistsByProductAndUserGetVm isOrderCompletedWithUserIdAndProductId(final String productId);

	List<OrderGetVm> getMyOrders(String productName, EOrderStatus orderStatus);

	PaymentOrderStatusVm updateOrderPaymentStatus(PaymentOrderStatusVm paymentOrderStatusVm)    ;

	void rejectOrder(Long orderId, String rejectReason);

	void acceptOrder(Long orderId);

	void updateStatusOrder(Long orderId, String statusOrder);

	void updateStatusOrder(Long orderId, String statusOrder, String token);

	void updateStatusDeliveryOrder(Long orderId, String statusOrderDelivery);

	List<OrderResponse> getOrderByUser(String phoneNumber, String status, String token);

	List<OrderResponse> getOrderBySeller(Long id, String status, String token);

	Order getOneOder(Long orderId);

	Order findOrderByCheckoutId(String checkoutId);
}