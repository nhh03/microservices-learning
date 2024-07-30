package com.nhh203.orderservice.repository;


import com.nhh203.orderservice.model.Checkout;
import com.nhh203.orderservice.model.enumeration.ECheckoutState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, String> {
	Optional<Checkout> findByIdAndCheckoutState(String id, ECheckoutState state);
}
