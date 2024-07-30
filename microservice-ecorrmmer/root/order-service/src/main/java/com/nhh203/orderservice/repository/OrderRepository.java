package com.nhh203.orderservice.repository;


import com.nhh203.orderservice.model.Order;
import com.nhh203.orderservice.model.enumeration.EOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByPhoneNumberAndOrderStatus(String phoneNumber, EOrderStatus statusOrder);

	List<Order> findByIdSellerAndOrderStatus(Long idSeller, EOrderStatus statusOrder);

	Optional<Order> findByCheckoutId(String checkoutId);

	@Query("SELECT (count(o) > 0) FROM Order o INNER JOIN o.orderItems oi " +
			"WHERE o.createdBy = :createdBy " +
			"AND o.orderStatus = 'COMPLETED' " +
			"AND oi.productId IN :productId")
	boolean existsByCreatedByAndInProductIdAndOrderStatusCompleted(
			String createdBy,
			List<String> productId
	);


	@Query("SELECT o FROM Order o JOIN o.orderItems oi " +
			"WHERE LOWER(o.email) LIKE %:email% " +
			"AND (o.orderStatus IN (:orderStatus)) " +
			"AND LOWER(oi.productName) LIKE %:productName% " +
			"AND LOWER(o.billingAddressId.phone) LIKE %:billingPhoneNumber% " +
			"AND LOWER(o.billingAddressId.countryName) LIKE %:countryName% " +
			"AND o.createdOn BETWEEN :createdFrom AND :createdTo " +
			"ORDER BY o.createdOn DESC")
	Page<Order> findOrderByWithMulCriteria(
			@Param("orderStatus") List<EOrderStatus> orderStatus,
			@Param("billingPhoneNumber") String billingPhoneNumber,
			@Param("countryName") String countryName,
			@Param("email") String email,
			@Param("productName") String productName,
			@Param("createdFrom") ZonedDateTime createdFrom,
			@Param("createdTo") ZonedDateTime createdTo,
			Pageable pageable
	);

	@Query("SELECT  distinct o from Order o  join  fetch  o.orderItems " +
			"where o.createdBy = :userId " +
			"and  (:orderStatus is null or o.orderStatus = :orderStatus) " +
			"and o.id in (select oi.orderId.id from OrderItem oi " +
			"where (:productName = '' or lower(oi.productName) like concat('%', lower(:productName) , '%')))")
	List<Order> findMyOrders(String userId, String productName, EOrderStatus orderStatus);

	@Modifying
	@Query("update  Order o set o.orderStatus = :statusOrder where o.id = :orderId")
	void updateStatusOrder(@Param("orderId") Long orderId, @Param("statusOrder") String statusOrder);

	@Modifying
	@Query("update  Order o set o.orderStatus = :statusDelivery where o.id = :orderId")
	void updateStatusDelivery(@Param("orderId") Long orderId, @Param("statusDelivery") String statusDelivery);
}