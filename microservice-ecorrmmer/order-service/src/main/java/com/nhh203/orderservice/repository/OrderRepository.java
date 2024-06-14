package com.nhh203.orderservice.repository;


import com.nhh203.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByPhoneNumberAndStatusOrder(String phoneNumber, String statusOrder);
    List<Order> findByIdSellerAndStatusOrder(Long idSeller, String statusOrder);
    @Modifying
    @Query("update  Order o set o.statusOrder = :statusOrder where o.id = :orderId")
    int updateStatusOrder(@Param("orderId") Long orderId, @Param("statusOrder") String statusOrder);

    @Modifying
    @Query("update  Order o set o.statusOrder = :statusDelivery where o.id = :orderId")
    int updateStatusDelivery(@Param("orderId") Long orderId, @Param("statusDelivery") String statusDelivery);
}