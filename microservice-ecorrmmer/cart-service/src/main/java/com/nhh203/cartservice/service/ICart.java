package com.nhh203.cartservice.service;

import com.nhh203.cartservice.model.dto.request.CartDTO;
import com.nhh203.cartservice.model.dto.response.CartResponse;
import com.nhh203.cartservice.model.dto.response.CartResponseWithMessage;
import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

public interface ICart {
    CartEntity createCartIExitsAddProduct(CartDTO cartDTO);

    CartEntity findCartByUser(Long iduser);

    void deleteCartItemById(Long id);

    List<CartResponse> findCartByUserHave(Long idUser, String token);

    CartResponseWithMessage getCartWithMessage(Long idUser, String token);

    void deleteByCartIdAndProductId(Long cartId, String productId);

    void deleteByCartIdAndProductIdIn(Long cartId, List<String> productIds);

    Integer countItemInCart(Long cartId);

    Set<CartItemEntity> findAllByCart(CartEntity cart);
    CartItemEntity findByCartIdAndProductId(Long cartId, String productId);
    void updateQuantityById(Long id, int quantity);


}
