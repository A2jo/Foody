package com.my.foody.domain.cart.service;

import com.my.foody.domain.cart.dto.req.CartMenuCreateReqDto;
import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.dto.resp.CartMenuCreateRespDto;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final StoreService storeService;

    public Page<CartItemRespDto> getCartItems(Long userId, int page, int limit) {

        //Active User의 존재 검증
        userService.findActivateUserByIdOrFail(userId);

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

    public CartMenuCreateRespDto addCartItem(Long storeId, Long menuId, CartMenuCreateReqDto cartMenuCreateReqDto, Long userId) {
        User user = userService.findActivateUserByIdOrFail(userId);
        Store store = storeService.findActivateStoreByIdOrFail(storeId);



    }
}
