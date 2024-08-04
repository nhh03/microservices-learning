package com.nhh203.cartservice.controllers;

import com.nhh203.cartservice.model.dto.request.CartDTO;
import com.nhh203.cartservice.model.dto.response.CartResponse;
import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import com.nhh203.cartservice.service.ICart;
import com.nhh203.cartservice.viewmodel.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.List;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CartController {
	ICart cartService;

	@GetMapping("/backoffice/carts/{customerId}")
	public ResponseEntity<CartEntity> getCartByCustomerId(@PathVariable String customerId) {
		CartEntity cart = cartService.findCartByUser(customerId);
		if (cart == null) {
			return ResponseEntity.notFound().build();
		}
		return new ResponseEntity<>(cart, HttpStatus.OK);
	}

	@GetMapping("/backoffice/carts")
	public ResponseEntity<List<CartListVm>> listCarts() {
		return ResponseEntity.ok(cartService.getCarts());
	}

	@GetMapping("/storefront/carts")
	public ResponseEntity<CartGetDetailVm> getLastCart(Principal principal) {
		if (principal == null)
			return ResponseEntity.ok(null);
		return ResponseEntity.ok(cartService.getLastCart(principal.getName()));
	}

	@Deprecated
	@PostMapping(path = "/storefront/carts/one")
	@Operation(summary = "Add product to shopping cart. When no cart exists, this will create a new cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Add to cart successfully", content = @Content(schema = @Schema(implementation = CartGetDetailVm.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<CartEntity> createCartAndAddProduct(@RequestBody CartDTO cartDTO) {
		CartEntity createdCart = cartService.createCartIExitsAddProduct(cartDTO);
		return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
	}

	@PostMapping(path = "/storefront/carts")
	@Operation(summary = "Add product to shopping cart. When no cart exists, this will create a new cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Add to cart successfully", content = @Content(schema = @Schema(implementation = CartGetDetailVm.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
	public ResponseEntity<CartGetDetailVm> createCart(
			@RequestBody @NotEmpty List<CartItemVm> cartItemVms
	) {
		CartGetDetailVm createdCart = cartService.addToCart(cartItemVms);
		return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
	}


	@Deprecated
	@DeleteMapping("/{cartId}/items/{productId}")
	public ResponseEntity<String> deleteCartItem(@PathVariable Long cartId, @PathVariable String productId) {
		this.cartService.deleteByCartIdAndProductId(cartId, productId);
		return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
	}

	@DeleteMapping("/storefront/cart-item")
	public ResponseEntity<Void> removeCartItemByProductId(@RequestParam String productId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		cartService.removeCartItemByProductId(productId, auth.getName());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/storefront/cart-item/multi-delete")
	public ResponseEntity<Void> removeCartItemListByProductIdList(@RequestParam List<String> productIds) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		cartService.removeCartItemListByProductIdList(productIds, auth.getName());
		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "/storefront/count-cart-items")
	public ResponseEntity<Long> getNumberItemInCart(Principal principal) {
		if (principal == null)
			return ResponseEntity.ok().body(0L);
		return ResponseEntity.ok().body(cartService.countNumberItemInCart(principal.getName()));
	}


	@GetMapping("/carts-user/{idUser}")
	public ResponseEntity<List<CartResponse>> findCartByUserHave(@PathVariable String idUser, @RequestHeader("Authorization") String token) {
		List<CartResponse> cartResponseList = cartService.findCartByUserHave(idUser, token);
		return new ResponseEntity<>(cartResponseList, HttpStatus.OK);
	}

	@GetMapping("/{customerId}/items")
	public ResponseEntity<Set<CartItemEntity>> getAllCartItems(@PathVariable String customerId) {
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
		if (cartItemEntity != null)
			return new ResponseEntity<>(cartItemEntity, HttpStatus.OK);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/cart-item/{id}")
	public ResponseEntity<String> deleteCartItemById(@PathVariable Long id) {
		cartService.deleteCartItemById(id);
		return ResponseEntity.ok("CartItemEntity with ID " + id + " has been successfully deleted.");
	}

	@PutMapping("cart-item")
	public ResponseEntity<CartItemPutVm> updateCart(@Valid @RequestBody CartItemVm cartItemVm) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return new ResponseEntity<>(cartService.updateCartItems(cartItemVm, auth.getName()), HttpStatus.OK);
	}

}
