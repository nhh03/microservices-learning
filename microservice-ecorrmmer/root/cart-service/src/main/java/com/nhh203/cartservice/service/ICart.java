package com.nhh203.cartservice.service;

import com.nhh203.cartservice.model.dto.request.CartDTO;
import com.nhh203.cartservice.model.dto.response.CartResponse;
import com.nhh203.cartservice.model.dto.response.CartResponseWithMessage;
import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import com.nhh203.cartservice.viewmodel.CartGetDetailVm;
import com.nhh203.cartservice.viewmodel.CartItemPutVm;
import com.nhh203.cartservice.viewmodel.CartItemVm;
import com.nhh203.cartservice.viewmodel.CartListVm;

import java.util.List;
import java.util.Set;

public interface ICart {
	CartEntity createCartIExitsAddProduct(CartDTO cartDTO);

	CartGetDetailVm addToCart(List<CartItemVm> cartItemVms);

	CartEntity findCartByUser(String iduser);

	CartGetDetailVm getLastCart(String customerId);

	List<CartListVm> getCarts();

	List<CartGetDetailVm> getCartDetailByCustomerId(String customerId);

	CartItemPutVm updateCartItems(CartItemVm cartItemVm, String customerId);

	void removeCartItemListByProductIdList(List<String> productIdList, String customerId);

	void removeCartItemByProductId(String productId, String customerId);

	Long countNumberItemInCart(String customerId);


	void deleteCartItemById(Long id);

	List<CartResponse> findCartByUserHave(String idUser, String token);

	CartResponseWithMessage getCartWithMessage(String idUser, String token);

	void deleteByCartIdAndProductId(Long cartId, String productId);

	void deleteByCartIdAndProductIdIn(Long cartId, List<String> productIds);

	Integer countItemInCart(Long cartId);

	Set<CartItemEntity> findAllByCart(CartEntity cart);

	CartItemEntity findByCartIdAndProductId(Long cartId, String productId);

	void updateQuantityById(Long id, int quantity);


}
