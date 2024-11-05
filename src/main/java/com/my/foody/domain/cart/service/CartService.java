package com.my.foody.domain.cart.service;

import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Page<CartItemRespDto> getCartItems(Long userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<Cart> cartItemsPage = cartRepository.findByUserId(userId, pageable);

    return cartItemsPage.map(
        cartItem -> {
          Long totalOrderAmount = cartItem.getMenu().getPrice() * cartItem.getQuantity();
          Long minOrderAmount = cartItem.getStore().getMinOrderAmount();

          return new CartItemRespDto(
              cartItem.getStore().getName(),
              cartItem.getMenu().getName(),
              cartItem.getMenu().getPrice(),
              cartItem.getQuantity(),
              totalOrderAmount,
              minOrderAmount);
        });
    }
}
