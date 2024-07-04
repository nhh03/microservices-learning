package com.nhh203.cartservice.service.impl;


import com.nhh203.cartservice.model.dto.request.CartDTO;
import com.nhh203.cartservice.model.dto.response.CartResponse;
import com.nhh203.cartservice.model.dto.response.CartResponseWithMessage;
import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import com.nhh203.cartservice.repository.CartItemRepository;
import com.nhh203.cartservice.repository.CartRepository;
import com.nhh203.cartservice.service.ICart;
import com.nhh203.commonservice.dto.ProductResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class CartImpl implements ICart {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    @PersistenceContext
    EntityManager entityManager;
    WebClient.Builder webClientBuilder;

    @Override
    public CartEntity createCartIExitsAddProduct(CartDTO cartDTO) {

        CartEntity cartExist = findCartByUser(cartDTO.getCustomerId());

        CartEntity cart;
        if (cartExist != null) {
            cart = cartExist;
        } else {
            cart = new CartEntity();
            cart.setCustomerId(cartDTO.getCustomerId());
        }

        boolean check = false;
        if (cartExist != null && cartExist.getCartItems() != null && !cartExist.getCartItems().isEmpty()) {
            for (CartItemEntity cartItem : cartExist.getCartItems()) {
                if (cartItem.getProductId().equals(cartDTO.getProductId())) {
                    check = true;
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                }
            }
        }

        if (check) {
            entityManager.persist(cart);
            return cart;
        }

        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setProductId(cartDTO.getProductId());
        cartItem.setQuantity(cartDTO.getQuantity());
        cartItem.setColor(cartDTO.getColor());
        cartItem.setSize(cartDTO.getSize());
        cart.getCartItems().add(cartItem);
        cartItem.setCart(cart);

        entityManager.persist(cart);
        entityManager.persist(cartItem);
        return cart;
    }

    @Override
    public CartEntity findCartByUser(Long iduser) {
        Optional<CartEntity> optionalCartEntity = cartRepository.findByCustomerId(iduser);
        return optionalCartEntity.isPresent() ? optionalCartEntity.get() : null;
    }

    @Override
    public void deleteCartItemById(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Override
    public List<CartResponse> findCartByUserHave(Long idUser, String token) {
        CartEntity cartEntity = findCartByUser(idUser);

        List<CartResponse> cartResponses = new ArrayList<>();
        if (cartEntity == null) {
            return cartResponses;
        }

        cartResponses = Flux.fromIterable(cartEntity.getCartItems()).flatMap(cartItem -> getProductResponse(cartItem.getProductId().toString(), token).map(productResponse -> {
            CartResponse cartResponse = new CartResponse();
            cartResponse.set_id(cartItem.getId().toString());
            cartResponse.setBuy_count(cartItem.getQuantity());
            cartResponse.setStatus(-1);
            cartResponse.setColor(cartItem.getColor());
            cartResponse.setSize(cartItem.getSize());
            cartResponse.setPrice(productResponse.getPrice());
            cartResponse.setPrice_before_discount(productResponse.getPrice());
            cartResponse.setProduct(productResponse);
            cartResponse.setUser(cartEntity.getCustomerId().toString());
            return cartResponse;
        })).collectList().block();
        return cartResponses;
    }

    private Mono<ProductResponse> getProductResponse(String productID, String token) {
        return webClientBuilder.build().get().uri("http://product-service/api/product/" + productID).retrieve().bodyToMono(ProductResponse.class);
    }

    @Override
    public CartResponseWithMessage getCartWithMessage(Long idUser, String token) {
        CartResponseWithMessage cartResponseWithMessage = new CartResponseWithMessage();
        List<CartResponse> list = findCartByUserHave(idUser, token);
        if (list != null) {
            cartResponseWithMessage.setMessage("Lấy giỏ hàng thành công");
            cartResponseWithMessage.setData(list);
            return cartResponseWithMessage;
        }
        return null;
    }

    @Override
    public void deleteByCartIdAndProductId(Long cartId, String productId) {
        cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
    }

    @Override
    public void deleteByCartIdAndProductIdIn(Long cartId, List<String> productIds) {
        cartItemRepository.deleteByCartIdAndProductIdIn(cartId, productIds);
    }

    @Override
    public Integer countItemInCart(Long cartId) {
        return cartItemRepository.countItemInCart(cartId);
    }

    @Override
    public Set<CartItemEntity> findAllByCart(CartEntity cart) {
        return this.cartItemRepository.findAllByCart(cart);
    }

    @Override
    public CartItemEntity findByCartIdAndProductId(Long cartId, String productId) {
        Optional<CartItemEntity> optionalCartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        return optionalCartItem.isPresent() ? optionalCartItem.get() : null;
    }

    @Override
    public void updateQuantityById(Long id, int quantity) {
        cartItemRepository.updateQuantityById(id, quantity);
    }


}
