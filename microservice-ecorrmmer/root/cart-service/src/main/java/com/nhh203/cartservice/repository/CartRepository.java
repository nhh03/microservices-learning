package com.nhh203.cartservice.repository;

import com.nhh203.cartservice.model.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
	Optional<CartEntity> findByCustomerId(String customerId);
	List<CartEntity> findByCustomerIdAndOrderIdIsNull(String customerId);
}
