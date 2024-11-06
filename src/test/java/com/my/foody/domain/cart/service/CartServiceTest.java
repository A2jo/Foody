package com.my.foody.domain.cart.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.my.foody.domain.cart.dto.req.CartMenuCreateReqDto;
import com.my.foody.domain.cart.dto.resp.CartMenuCreateRespDto;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;

import java.util.Optional;

import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        when(menuService.findActiveMenuByIdOrFail(menuId)).thenReturn(menu);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartMenuRepository.save(any(CartMenu.class))).thenReturn(cartMenu);

        // when
        CartMenuCreateRespDto result = cartService.addCartItem(storeId, menuId, request, userId);

        // then
        assertNotNull(result);
        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(menuService).findActiveMenuByIdOrFail(menuId);
        verify(cartRepository).findByUser(user);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMenuRepository).save(any(CartMenu.class));
    }


    @Test
    @DisplayName("장바구니에 메뉴 추가 성공 테스트: 새로운 장바구니 생성")
    void addCartItem_NewCart_Success() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);

        User user = mock(User.class);
        Store store = mock(Store.class);
        Menu menu = mock(Menu.class);
        Cart newCart = mock(Cart.class);
        CartMenu cartMenu = mock(CartMenu.class);

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(menuService.findActiveMenuByIdOrFail(menuId)).thenReturn(menu);
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(cartMenuRepository.save(any(CartMenu.class))).thenReturn(cartMenu);

        // when
        CartMenuCreateRespDto result = cartService.addCartItem(storeId, menuId, request, userId);

        // then
        assertNotNull(result);
        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(menuService).findActiveMenuByIdOrFail(menuId);
        verify(cartRepository).findByUser(user);
        verify(cartRepository).save(any(Cart.class));
        verify(cartMenuRepository).save(any(CartMenu.class));
    }



    @Test
    @DisplayName("장바구니에 메뉴 추가 실패 테스트: 존재하지 않는 사용자")
    void addCartItem_UserNotFound() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);

        when(userService.findActivateUserByIdOrFail(userId))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() ->
                cartService.addCartItem(storeId, menuId, request, userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService, never()).findActivateStoreByIdOrFail(anyLong());
        verify(menuService, never()).findActiveMenuByIdOrFail(anyLong());
        verify(cartRepository, never()).findByUser(any(User.class));
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMenuRepository, never()).save(any(CartMenu.class));
    }


    @Test
    @DisplayName("장바구니에 메뉴 추가 실패 테스트: 존재하지 않는 가게")
    void addCartItem_StoreNotFound() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);
        User user = mock(User.class);

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId))
                .thenThrow(new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // when & then
        assertThatThrownBy(() ->
                cartService.addCartItem(storeId, menuId, request, userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STORE_NOT_FOUND);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(menuService, never()).findActiveMenuByIdOrFail(anyLong());
        verify(cartRepository, never()).findByUser(any(User.class));
        verify(cartMenuRepository, never()).save(any(CartMenu.class));
    }


    @Test
    @DisplayName("장바구니에 메뉴 추가 실패 테스트:  품절된 메뉴")
    void addCartItem_MenuNotAvailable() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);
        User user = mock(User.class);
        Store store = mock(Store.class);

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(menuService.findActiveMenuByIdOrFail(menuId))
                .thenThrow(new BusinessException(ErrorCode.MENU_NOT_AVAILABLE));

        // when & then
        assertThatThrownBy(() ->
                cartService.addCartItem(storeId, menuId, request, userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MENU_NOT_AVAILABLE);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(menuService).findActiveMenuByIdOrFail(menuId);
        verify(cartRepository, never()).findByUser(any(User.class));
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMenuRepository, never()).save(any(CartMenu.class));
    }


}

