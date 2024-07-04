package com.nhh203.cartservice.repository;

import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    @Transactional
    void deleteByCartIdAndProductId(Long cartId, String productId);

    @Transactional
    void deleteByCartIdAndProductIdIn(Long cartId, List<String> productIds);


    @Query("select sum(c.quantity) from CartItemEntity c where c.cart.id = ?1")
    Integer countItemInCart(Long cartId);

    @Modifying
    @Transactional
    @Query("UPDATE CartItemEntity c SET c.quantity = :quantity WHERE c.id = :id")
    void updateQuantityById(Long id, int quantity);

    Set<CartItemEntity> findAllByCart(CartEntity cart);

    Optional<CartItemEntity> findByCartIdAndProductId(Long cartId, String productId);

}
