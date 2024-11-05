package com.my.foody.domain.cart.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.my.foody.domain.cart.dto.req.CartMenuCreateReqDto;
import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.dto.resp.CartMenuCreateRespDto;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import java.util.Arrays;
import java.util.Optional;

import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest extends DummyObject {


    @InjectMocks
    private CartService cartService;

    @Mock
    private UserService userService;
    @Mock
    private StoreService storeService;
    @Mock
    private MenuService menuService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartMenuRepository cartMenuRepository;

    @Test
    @DisplayName("장바구니에 메뉴 추가 성공 테스트: 장바구니가 이미 존재하는 경우")
    void addCartItem_ExistingCart_Success() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);

        User user = mock(User.class);
        Store store = mock(Store.class);
        Menu menu = mock(Menu.class);
        Cart cart = mock(Cart.class);
        CartMenu cartMenu = mock(CartMenu.class);

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(menuService.findByIdOrFail(menuId)).thenReturn(menu);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartMenuRepository.save(any(CartMenu.class))).thenReturn(cartMenu);

        // when
        CartMenuCreateRespDto result = cartService.addCartItem(storeId, menuId, request, userId);

        // then
        assertNotNull(result);
        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(menuService).findByIdOrFail(menuId);
        verify(cartRepository).findByUser(user);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMenuRepository).save(any(CartMenu.class));
    }



}

