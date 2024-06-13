package com.nhh203.cartservice.controllers;

import com.nhh203.cartservice.model.dto.request.CartDTO;
import com.nhh203.cartservice.model.dto.response.CartResponse;
import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import com.nhh203.cartservice.service.ICart;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CartController {
    ICart cartService;

    @PostMapping("/create")
    public ResponseEntity<CartEntity> createCartAndAddProduct(@RequestBody CartDTO cartDTO) {
        CartEntity createdCart = cartService.createCartIExitsAddProduct(cartDTO);
        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartEntity> getCartByCustomerId(@PathVariable Long customerId) {
        CartEntity cart = cartService.findCartByUser(customerId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long cartId, @PathVariable String productId) {
        this.cartService.deleteByCartIdAndProductId(cartId, productId);
        return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
    }

    @GetMapping("/carts-user/{idUser}")
    public ResponseEntity<List<CartResponse>> findCartByUserHave(@PathVariable Long idUser, @RequestHeader("Authorization") String token) {
        List<CartResponse> cartResponseList = cartService.findCartByUserHave(idUser, token);
        return new ResponseEntity<>(cartResponseList, HttpStatus.OK);
    }

    @GetMapping("/{customerId}/items")
    public ResponseEntity<Set<CartItemEntity>> getAllCartItems(@PathVariable Long customerId) {
        CartEntity cart = cartService.findCartByUser(customerId);
        if (cart != null) {
            Set<CartItemEntity> cartItems = cartService.findAllByCart(cart);
            return new ResponseEntity<>(cartItems, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> deleteCartItems(@PathVariable Long cartId, @RequestBody List<String> productIds) {
        cartService.deleteByCartIdAndProductIdIn(cartId, productIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{cartId}/items/count")
    public ResponseEntity<Integer> countItemsInCart(@PathVariable Long cartId) {
        Integer count = cartService.countItemInCart(cartId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @GetMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartItemEntity> getCartItem(@PathVariable Long cartId, @PathVariable String productId) {
        CartItemEntity cartItemEntity = cartService.findByCartIdAndProductId(cartId, productId);
        if (cartItemEntity != null) return new ResponseEntity<>(cartItemEntity, HttpStatus.OK);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart-item/{id}")
    public ResponseEntity<String> deleteCartItemById(@PathVariable Long id) {
        cartService.deleteCartItemById(id);
        return ResponseEntity.ok("CartItemEntity with ID " + id + " has been successfully deleted.");
    }

}
