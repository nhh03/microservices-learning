package com.nhh203.orderservice.service.impl;


import com.nhh203.orderservice.dto.OrderLineItemResponse;
import com.nhh203.orderservice.dto.OrderLineItemsRequest;
import com.nhh203.orderservice.dto.OrderRequest;
import com.nhh203.orderservice.dto.OrderResponse;
import com.nhh203.orderservice.exception.NotFoundException;
import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.model.OrderAddress;
import com.nhh203.orderservice.model.OrderItem;
import com.nhh203.orderservice.model.enumeration.EDeliveryStatus;
import com.nhh203.orderservice.model.enumeration.EOrderStatus;
import com.nhh203.orderservice.model.enumeration.EPaymentStatus;
import com.nhh203.orderservice.repository.OrderItemRepository;
import com.nhh203.orderservice.repository.OrderRepository;
import com.nhh203.orderservice.service.CartService;
import com.nhh203.orderservice.service.OrderService;
import com.nhh203.orderservice.service.ProductService;
import com.nhh203.orderservice.utils.AuthenticationUtils;
import com.nhh203.orderservice.utils.Constants;
import com.nhh203.orderservice.viewmodel.order.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

import com.nhh203.orderservice.model.enumeration.EOrderStatus;
import com.nhh203.orderservice.model.enumeration.EPaymentStatus;
import com.nhh203.orderservice.viewmodel.product.ProductVariationVM;

import static com.nhh203.orderservice.utils.Constants.ERROR_CODE.ORDER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductService productService;
	private final CartService cartService;
	private final WebClient.Builder webClientBuilder;
	@PersistenceContext
	private final EntityManager entityManager;


	@Override
	public Order createOder(OrderRequest orderRequest) {

		//        Order order = Order.builder()
		//                .phoneNumber(orderRequest.getPhoneNumber())
		//                .address(orderRequest.getAddress())
		//                .statusDelivery(orderRequest.getStatusDelivery())
		//                .statusOrder(orderRequest.getStatusOrder())
		//                .totalMoney(orderRequest.getTotalMoney())
		//                .idSeller(orderRequest.getIdSeller()).build();
		//
		//        List<OrderItem> orderItemsEntityList = orderRequest.getOrderLineItemsRequestList().stream().map(orderLineItemsDto -> {
		//            OrderItem orderItem = MapperOrder.mapToOrderLineItems(orderLineItemsDto);
		//            orderItem.setOrderId(order);
		//            return orderItem;
		//        }).toList();
		//
		//        order.setOrderItemList(orderItemsEntityList);
		//        entityManager.persist(order);
		//        return order;
		return null;
	}

	@Override
	public OrderVm createOrder(OrderPostVm orderPostVm) {
		OrderAddressPostVm billingAddressPostVm = orderPostVm.billingAddressPostVm();
		OrderAddress billOrderAddress = OrderAddress.builder()
				.phone(billingAddressPostVm.phone())
				.contactName(billingAddressPostVm.contactName())
				.addressLine1(billingAddressPostVm.addressLine1())
				.addressLine2(billingAddressPostVm.addressLine2())
				.city(billingAddressPostVm.city())
				.zipCode(billingAddressPostVm.zipCode())
				.districtId(billingAddressPostVm.districtId())
				.districtName(billingAddressPostVm.districtName())
				.stateOrProvinceId(billingAddressPostVm.stateOrProvinceId())
				.stateOrProvinceName(billingAddressPostVm.stateOrProvinceName())
				.countryId(billingAddressPostVm.countryId())
				.countryName(billingAddressPostVm.countryName())
				.build();

		OrderAddressPostVm shipOrderAddressPostVm = orderPostVm.shippingAddressPostVm();
		OrderAddress shippOrderAddress = OrderAddress.builder()
				.phone(shipOrderAddressPostVm.phone())
				.contactName(shipOrderAddressPostVm.contactName())
				.addressLine1(shipOrderAddressPostVm.addressLine1())
				.addressLine2(shipOrderAddressPostVm.addressLine2())
				.city(shipOrderAddressPostVm.city())
				.zipCode(shipOrderAddressPostVm.zipCode())
				.districtId(shipOrderAddressPostVm.districtId())
				.districtName(shipOrderAddressPostVm.districtName())
				.stateOrProvinceId(shipOrderAddressPostVm.stateOrProvinceId())
				.stateOrProvinceName(shipOrderAddressPostVm.stateOrProvinceName())
				.countryId(shipOrderAddressPostVm.countryId())
				.countryName(shipOrderAddressPostVm.countryName())
				.build();

		Order order = Order.builder()
				.checkoutId(orderPostVm.checkoutId())
				.email(orderPostVm.email())
				.shippingAddressId(shippOrderAddress)
				.billingAddressId(billOrderAddress)
				.note(orderPostVm.note())
				.tax(orderPostVm.tax())
				.discount(orderPostVm.discount())
				.numberItem(orderPostVm.numberItem())
				.couponCode(orderPostVm.couponCode())
				.totalPrice(orderPostVm.totalPrice())
				.deliveryFee(orderPostVm.deliveryFee())
				.phoneNumber(orderPostVm.phoneNumber())
				.idSeller(orderPostVm.idSeller())
				.deliveryMethod(orderPostVm.deliveryMethod())
				.deliveryStatus(EDeliveryStatus.PREPARING)
				.paymentStatus(orderPostVm.paymentStatus())
				.build();
		orderRepository.save(order);
		Set<OrderItem> orderItems = orderPostVm.orderItemPostVms().stream()
				.map(item -> OrderItem.builder()
						.productId(item.productId())
						.productName(item.productName())
						.quantity(item.quantity())
						.productPrice(item.productPrice())
						.note(item.note())
						.orderId(order)
						.build())
				.collect(Collectors.toSet());
		orderItemRepository.saveAll(orderItems);
		order.setOrderItems(orderItems);
		OrderVm orderVm = OrderVm.fromModel(order);
		productService.subtractProductStockQuantity(orderVm);
		cartService.deleteCartItem(orderVm);
		acceptOrder(order.getId());
		return orderVm;
	}

	@Override
	public OrderVm getOrderWithItemsById(long id) {
		Order order = orderRepository.findById(id).orElseThrow(()
				-> new NotFoundException(ORDER_NOT_FOUND, id));
		return OrderVm.fromModel(order);
	}

	@Override
	public OrderListVm getAllOrder(
			ZonedDateTime createdFrom,
			ZonedDateTime createdTo,
			String warehouse,
			String productName,
			List<EOrderStatus> orderStatus,
			String billingCountry,
			String billingPhoneNumber,
			String email,
			int pageNo,
			int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		List<EOrderStatus> allOrderStatus = Arrays.asList(EOrderStatus.values());
		Page<Order> orderPage = orderRepository.findOrderByWithMulCriteria(
				orderStatus.isEmpty() ? allOrderStatus : orderStatus,
				billingPhoneNumber,
				billingCountry,
				email.toLowerCase(),
				productName.toLowerCase(),
				createdFrom,
				createdTo,
				pageable);
		if (orderPage.isEmpty())
			return new OrderListVm(null, 0, 0);
		List<OrderBriefVm> orderVms = orderPage.getContent()
				.stream()
				.map(OrderBriefVm::fromModel)
				.toList();
		return new OrderListVm(orderVms, orderPage.getTotalElements(), orderPage.getTotalPages());
	}

	@Override
	public OrderExistsByProductAndUserGetVm isOrderCompletedWithUserIdAndProductId(String productId) {
		String userId = AuthenticationUtils.getCurrentUserId();
		List<ProductVariationVM> productVariations = productService.getProductVariations(productId);
		List<String> productIds;
		if (CollectionUtils.isEmpty(productVariations)) {
			productIds = Collections.singletonList(productId);
		} else {
			productIds = productVariations.stream().map(ProductVariationVM::id).toList();
		}
		return new OrderExistsByProductAndUserGetVm(
				orderRepository.existsByCreatedByAndInProductIdAndOrderStatusCompleted(userId, productIds)
		);
	}

	@Override
	public List<OrderGetVm> getMyOrders(String productName, EOrderStatus orderStatus) {
		String userId = AuthenticationUtils.getCurrentUserId();
		List<Order> orders = orderRepository.findMyOrders(userId, productName, orderStatus);
		return orders.stream().map(OrderGetVm::fromModel).collect(Collectors.toList());
	}

	@Override
	public PaymentOrderStatusVm updateOrderPaymentStatus(PaymentOrderStatusVm paymentOrderStatusVm) {
		var order = this.orderRepository
				.findById(paymentOrderStatusVm.orderId())
				.orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, paymentOrderStatusVm.orderId()));
		order.setPaymentId(paymentOrderStatusVm.paymentId());
		order.setPaymentStatus(EPaymentStatus.valueOf(paymentOrderStatusVm.paymentStatus()));
		if (EPaymentStatus.COMPLETED.name().equals(paymentOrderStatusVm.paymentStatus())) {
			order.setOrderStatus(EOrderStatus.PAID);
		}
		Order result = this.orderRepository.save(order);
		return PaymentOrderStatusVm.builder()
				.orderId(result.getId())
				.orderStatus(result.getOrderStatus().name())
				.paymentId(result.getPaymentId())
				.paymentStatus(result.getPaymentStatus().name())
				.build();
	}

	@Override
	public void rejectOrder(Long orderId, String rejectReason) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, "of orderId " + orderId));
		order.setOrderStatus(EOrderStatus.REJECT);
		order.setRejectReason(rejectReason);
		orderRepository.save(order);
	}

	@Override
	public void acceptOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, "of orderId " + orderId));
		order.setOrderStatus(EOrderStatus.ACCEPTED);
		orderRepository.save(order);
	}

	@Override
	public void updateStatusOrder(Long orderId, String statusOrder) {
		orderRepository.updateStatusOrder(orderId, statusOrder);
	}

	@Override
	public void updateStatusOrder(Long orderId, String statusOrder, String token) {
		orderRepository.updateStatusOrder(orderId, statusOrder);
	}

	@Override
	public void updateStatusDeliveryOrder(Long orderId, String statusOrderDelivery) {
		orderRepository.updateStatusDelivery(orderId, statusOrderDelivery);
	}

	@Override
	public List<OrderResponse> getOrderByUser(String phoneNumber, String status, String token) {

		//        String query = getStatusQuery(status);
		//        List<Order> orders = orderRepository.findByPhoneNumberAndStatusOrder(phoneNumber, query);
		//        List<OrderResponse> orderResponses = orders.stream()
		//                .map(MapperOrder::mapToOrderResponse)
		//                .collect(Collectors.toList());
		//        List<Mono<Void>> products = orders.stream()
		//                .flatMap(orderResponse -> orderResponse.getOrderItemList().stream())
		//                .map(orderLineItemResponse -> getOrderLineItemResponse(orderLineItemResponse.getProductId(), token)
		//                        .doOnNext(orderLineItemResponse1 -> OrderLineItemResponse
		//                                .builder()
		//                                .img(orderLineItemResponse1.getImg())
		//                                .name(orderLineItemResponse1.getName())
		//                                .note(orderLineItemResponse.getNote())
		//                                .build())
		//                        .then()
		//                ).collect(Collectors.toList());
		//
		//        Mono.when(products).block();
		//
		//        return orderResponses;
		return null;
	}

	private Mono<OrderLineItemResponse> getOrderLineItemResponse(String productId, String token) {
		return webClientBuilder.build().get().uri("http://product-service/api/product/" + productId).retrieve().bodyToMono(OrderLineItemResponse.class);
	}


	@Override
	public List<OrderResponse> getOrderBySeller(Long id, String status, String token) {
		//        String query = getStatusQuery(status);
		//        List<Order> orders = orderRepository.findByIdSellerAndStatusOrder(id, query);
		//        List<OrderResponse> orderResponses = orders.stream()
		//                .map(MapperOrder::mapToOrderResponse)
		//                .collect(Collectors.toList());
		//
		//        List<OrderLineItemResponse> allOrderLineItems = orders.stream()
		//                .flatMap(order -> order.getOrderItemList().stream())
		//                .map(orderLineItems -> OrderLineItemResponse.builder()
		//                        .productId(orderLineItems.getProductId())
		//                        .quantity(orderLineItems.getQuantity())
		//                        .price(orderLineItems.getPrice())
		//                        .note(orderLineItems.getNote())
		//                        .build())
		//                .collect(Collectors.toList());
		//
		//
		//        Flux.fromIterable(allOrderLineItems)
		//                .flatMap(orderLineItemResponse -> getOrderLineItemResponse(orderLineItemResponse.getProductId(), token)
		//                        .doOnNext(orderLineItemResponse1 -> OrderLineItemResponse
		//                                .builder()
		//                                .img(orderLineItemResponse1.getImg())
		//                                .name(orderLineItemResponse1.getName())
		//                                .note(orderLineItemResponse.getNote())
		//                                .build())
		//                        .then()
		//                ).then().block();
		//
		//        return orderResponses;


		return null;
	}

	@Override
	public Order getOneOder(Long orderId) {
		return orderRepository.findById(orderId).orElse(null);
	}

	@Override
	public Order findOrderByCheckoutId(String checkoutId) {
		return orderRepository.findByCheckoutId(checkoutId)
				.orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND, "of checkoutId " + checkoutId));
	}

	private String getStatusQuery(String status) {
		switch (status) {
			case "1":
				return "Chờ xác nhận";
			case "2":
				return "Chờ lấy hàng";
			case "3":
				return "Chờ giao hàng";
			case "4":
				return "Hoàn thành";
			case "5":
				return "Đã hủy";
			case "6":
				return "Trả hàng/Hoàn tiền";
			default:
				return "";
		}
	}


	private String placeOrder(OrderRequest orderRequest) {
		//        Order order = new Order();
		//        order.setOrderNumber(UUID.randomUUID().toString());
		//
		//
		//        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
		//                .stream()
		//                .map(this::mapToDto)
		//                .toList();
		//
		//        order.setOrderLineItemsList(orderLineItems);
		//
		//        List<String> skuCodes = order.getOrderLineItemsList().stream()
		//                .map(OrderLineItems::getSkuCode)
		//                .toList();
		//
		//        Span inventoryServiceLookup = tracer.nextSpan().name("inventoryServiceLookup");
		//
		//        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
		//
		//            // call to  inventoryService  and place order if product is in stock
		//            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
		//                    .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
		//                    .retrieve()
		//                    .bodyToMono(InventoryResponse[].class)
		//                    .block();
		//
		//            if (inventoryResponseArray.length > 0) {
		//                boolean allProductInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
		//                if (allProductInStock) {
		//                    orderRepository.save(order);
		//                    kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
		//                    return "Order Placed";
		//                } else {
		//                    throw new IllegalArgumentException("Product is not in stock , please try again later ");
		//                }
		//            } else {
		//                throw new IllegalArgumentException("Empty");
		//
		//            }
		//        } finally {
		//
		//            inventoryServiceLookup.end();
		//
		//        }

		return "ok";

	}


	private OrderItem mapToDto(OrderLineItemsRequest orderLineItemsRequest) {
		OrderItem orderItem = new OrderItem();
		//        orderLineItems.setPrice(orderLineItemsDto.getPrice());
		//        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		//        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderItem;
	}

}
