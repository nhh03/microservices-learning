package com.nhh203.cartservice.service.impl;


import com.nhh203.cartservice.exception.BadRequestException;
import com.nhh203.cartservice.exception.NotFoundException;
import com.nhh203.cartservice.model.dto.request.CartDTO;
import com.nhh203.cartservice.model.dto.response.CartResponse;
import com.nhh203.cartservice.model.dto.response.CartResponseWithMessage;
import com.nhh203.cartservice.model.dto.response.ProductResponse;
import com.nhh203.cartservice.model.entity.CartEntity;
import com.nhh203.cartservice.model.entity.CartItemEntity;
import com.nhh203.cartservice.repository.CartItemRepository;
import com.nhh203.cartservice.repository.CartRepository;
import com.nhh203.cartservice.service.ICart;
import com.nhh203.cartservice.service.ProductService;
import com.nhh203.cartservice.utils.Constants;
import com.nhh203.cartservice.viewmodel.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class CartImpl implements ICart {
	@PersistenceContext
	EntityManager entityManager;
	CartRepository cartRepository;
	CartItemRepository cartItemRepository;
	ProductService productService;
	WebClient.Builder webClientBuilder;

	private static final String CART_ITEM_UPDATED_MSG = "PRODUCT %s";

	@Override
	public CartEntity createCartIExitsAddProduct(CartDTO cartDTO) {

		CartEntity cartExist = findCartByUser(cartDTO.getCustomerId());

		CartEntity cart;
		if (cartExist != null) {
			cart = cartExist;
		} else {
			cart = new CartEntity();
			cart.setCustomerId(cartDTO.getCustomerId().toString());
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
	public CartGetDetailVm addToCart(List<CartItemVm> cartItemVms) {
		List<String> productIds = cartItemVms.stream().map(CartItemVm::productId).toList();
		List<ProductThumbnailVm> productThumbnailVmList = productService.getProducts(productIds);
		if (productThumbnailVmList.size() != productIds.size()) {
			throw new NotFoundException(Constants.ERROR_CODE.NOT_FOUND_PRODUCT);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String customerId = auth.getName();
		CartEntity cartEntity = cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)
				.stream()
				.findFirst()
				.orElse(null);
		Set<CartItemEntity> existedCartItemEntities = new HashSet<>();

		if (cartEntity != null) {
			existedCartItemEntities = cartEntity.getCartItems();
		} else {
			cartEntity = CartEntity.builder()
					.customerId(customerId)
					.cartItems(existedCartItemEntities)
					.build();
			cartEntity.setCreatedOn(ZonedDateTime.now());
		}
		for (CartItemVm cartItemVm : cartItemVms) {
			CartItemEntity cartItemEntity = getCartItemByProductId(existedCartItemEntities, cartItemVm.productId());
			if (cartItemEntity.getId() == null) {
				cartItemEntity.setProductId(cartItemVm.productId());
				cartItemEntity.setQuantity(cartItemVm.quantity());
				cartItemEntity.setCart(cartEntity);
				cartItemEntity.setParentProductId(cartItemVm.parentProductId());
				cartEntity.getCartItems().add(cartItemEntity);
			} else {
				cartItemEntity.setQuantity(cartItemEntity.getQuantity() + cartItemVm.quantity());
			}
		}
		cartEntity = cartRepository.save(cartEntity);
		cartItemRepository.saveAll(existedCartItemEntities);
		return CartGetDetailVm.fromModel(cartEntity);
	}

	@Override
	public CartEntity findCartByUser(String iduser) {
		Optional<CartEntity> optionalCartEntity = cartRepository.findByCustomerId(iduser);
		return optionalCartEntity.isPresent() ? optionalCartEntity.get() : null;
	}

	@Override
	public CartGetDetailVm getLastCart(String customerId) {
		return cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)
				.stream().reduce((first, second) -> second)
				.map(CartGetDetailVm::fromModel)
				.orElse(CartGetDetailVm.fromModel(new CartEntity()));
	}

	@Override
	public List<CartListVm> getCarts() {
		return cartRepository.findAll().stream().map(CartListVm::fromModel).toList();
	}

	@Override
	public List<CartGetDetailVm> getCartDetailByCustomerId(String customerId) {
		return cartRepository.findByCustomerId(customerId).stream().map(CartGetDetailVm::fromModel).toList();
	}

	@Override
	public CartItemPutVm updateCartItems(CartItemVm cartItemVm, String customerId) {
		CartGetDetailVm currentCart = getLastCart(customerId);
		validateCart(currentCart, cartItemVm.productId());

		Long cartId = currentCart.id();
		CartItemEntity cartItemEntity = findByCartIdAndProductId(cartId, cartItemVm.productId());
		if (cartItemEntity == null) {
			throw new NotFoundException(Constants.ERROR_CODE.NOT_EXISTING_ITEM_IN_CART + cartId);
		}
		int newQuantity = cartItemVm.quantity();
		cartItemEntity.setQuantity(newQuantity);
		if (newQuantity == 0) {
			deleteByCartIdAndProductId(cartId, cartItemVm.productId());
			return CartItemPutVm.fromModel(cartItemEntity, String.format(CART_ITEM_UPDATED_MSG, "DELETED"));
		} else {
			updateQuantityById(cartItemEntity.getId(), newQuantity);
			return CartItemPutVm.fromModel(cartItemEntity, String.format(CART_ITEM_UPDATED_MSG, "UPDATED"));
		}
	}

	@Override
	public void removeCartItemListByProductIdList(List<String> productIdList, String customerId) {
		CartGetDetailVm currentCart = getLastCart(customerId);
		productIdList.forEach(productId -> validateCart(currentCart, productId));
		cartItemRepository.deleteByCartIdAndProductIdIn(currentCart.id(), productIdList);
	}

	@Override
	public void removeCartItemByProductId(String productId, String customerId) {
		CartGetDetailVm currentCart = getLastCart(customerId);
		validateCart(currentCart, productId);
		cartItemRepository.deleteByCartIdAndProductId(currentCart.id(), productId);
	}

	@Override
	public Long countNumberItemInCart(String customerId) {
		Optional<CartEntity> cartOp = cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)
				.stream().reduce((first, second) -> second);
		if (cartOp.isEmpty()) {
			return 0L;
		}
		var cart = cartOp.get();
		Integer total = cartItemRepository.countItemInCart(cart.getId());
		if (total == null) {
			return 0L;
		}
		return total.longValue();
	}

	@Override
	public void deleteCartItemById(Long id) {
		cartItemRepository.deleteById(id);
	}

	@Override
	public List<CartResponse> findCartByUserHave(String idUser, String token) {
		CartEntity cartEntity = findCartByUser(idUser);
		List<CartResponse> cartResponses = new ArrayList<>();
		if (cartEntity == null) {
			return cartResponses;
		}

		cartResponses = Flux.fromIterable(cartEntity.getCartItems())
				.flatMap(cartItem -> getProductResponse(cartItem.getProductId().toString(), token).map(productResponse -> {
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
				}))
				.collectList()
				.block();
		return cartResponses;
	}

	private Mono<ProductResponse> getProductResponse(String productID, String token) {
		return webClientBuilder
				.build()
				.get()
				.uri("http://product-service/api/product/" + productID)
				.retrieve()
				.bodyToMono(ProductResponse.class);
	}

	@Override
	public CartResponseWithMessage getCartWithMessage(String idUser, String token) {
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
		return optionalCartItem.orElse(null);
	}

	@Override
	public void updateQuantityById(Long id, int quantity) {
		cartItemRepository.updateQuantityById(id, quantity);
	}


	private void validateCart(CartGetDetailVm cart, String productId) {
		if (cart.cartDetails().isEmpty()) {
			throw new BadRequestException(Constants.ERROR_CODE.NOT_EXISTING_ITEM_IN_CART);
		}
		List<CartDetailVm> cartDetailVmList = cart.cartDetails();
		boolean itemExist = cartDetailVmList.stream()
				.anyMatch(cartDetailVm -> cartDetailVm.productId().equals(productId));
		if (!itemExist) {
			throw new NotFoundException(Constants.ERROR_CODE.NOT_EXISTING_ITEM_IN_CART);
		}
	}

	private CartItemEntity getCartItemByProductId(Set<CartItemEntity> cartItemEntities, String productId) {
		for (CartItemEntity cartItemEntity : cartItemEntities) {
			if (cartItemEntity.getProductId().equals(productId)) {
				return cartItemEntity;
			}
		}
		return new CartItemEntity();
	}


}
