package com.my.foody.domain.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("장바구니에 메뉴 추가 성공 테스트: 새로운 장바구니 생성")
    void addCartItem_CreateNewCart_Success() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);

        User user = newUser(userId);
        Store store = createStore(storeId);
        Menu menu = createMenu(menuId, store);
        Cart cart = createCart(user, store);
        CartMenu cartMenu = createCartMenu(cart, menu, request.getQuantity());

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(menuService.findActiveMenuByIdOrFail(menuId)).thenReturn(menu);
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMenuRepository.save(any(CartMenu.class))).thenReturn(cartMenu);

        // When
        CartMenuCreateRespDto result = cartService.addCartItem(storeId, menuId, request, userId);

        // Then
        assertThat(result).isNotNull();
        verify(cartRepository).findByUser(user);
        verify(cartRepository).save(any(Cart.class));
        verify(cartMenuRepository).save(any(CartMenu.class));
        verify(cartMenuRepository, never()).deleteByCart(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니에 메뉴 추가 성공: 같은 가게의 메뉴 추가")
    void addCartItem_SameStore_Success() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);

        User user = newUser(userId);
        Store store = createStore(storeId);
        Menu menu = createMenu(menuId, store);
        Cart existingCart = createCart(user, store);
        CartMenu cartMenu = createCartMenu(existingCart, menu, request.getQuantity());

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(menuService.findActiveMenuByIdOrFail(menuId)).thenReturn(menu);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(existingCart));
        when(cartMenuRepository.save(any(CartMenu.class))).thenReturn(cartMenu);

        // When
        CartMenuCreateRespDto result = cartService.addCartItem(storeId, menuId, request, userId);

        // Then
        assertThat(result).isNotNull();
        verify(cartMenuRepository, never()).deleteByCart(any(Cart.class));
        verify(cartMenuRepository).save(any(CartMenu.class));
    }

    @Test
    @DisplayName("장바구니에 메뉴 추가 성공 테스트: 다른 가게의 메뉴 추가 (장바구니 초기화)")
    void addCartItem_DifferentStore_Success() {
        // Given
        Long userId = 1L;
        Long oldStoreId = 1L;
        Long newStoreId = 2L;
        Long menuId = 2L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);

        User user = newUser(userId);
        Store oldStore = createStore(oldStoreId);
        Store newStore = createStore(newStoreId);
        Menu menu = createMenu(menuId, newStore);
        Cart existingCart = createCart(user, oldStore);
        CartMenu cartMenu = createCartMenu(existingCart, menu, request.getQuantity());

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(newStoreId)).thenReturn(newStore);
        when(menuService.findActiveMenuByIdOrFail(menuId)).thenReturn(menu);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(existingCart));
        when(cartMenuRepository.save(any(CartMenu.class))).thenReturn(cartMenu);

        // When
        CartMenuCreateRespDto result = cartService.addCartItem(newStoreId, menuId, request, userId);

        // Then
        assertThat(result).isNotNull();
        verify(cartMenuRepository).deleteByCart(existingCart);
        verify(cartMenuRepository).save(any(CartMenu.class));
        assertThat(existingCart.getStore()).isEqualTo(newStore);
    }

    @Test
    @DisplayName("장바구니 메뉴 추가 실패 테스트: 메뉴와 가게 불일치")
    void addCartItem_MenuStoreMismatch_Fail() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;
        Long menuId = 1L;
        CartMenuCreateReqDto request = new CartMenuCreateReqDto(2L);

        User user = newUser(userId);
        Store requestStore = createStore(storeId);
        Store menuStore = createStore(2L);
        Menu menu = createMenu(menuId, menuStore);

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(requestStore);
        when(menuService.findActiveMenuByIdOrFail(menuId)).thenReturn(menu);

        // When & Then
        assertThatThrownBy(() -> cartService.addCartItem(storeId, menuId, request, userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MENU_STORE_MISMATCH);
    }


    private Store createStore(Long id) {
        return Store.builder()
                .id(id)
                .name("Test Store " + id)
                .build();
    }

    private Menu createMenu(Long id, Store store) {
        return Menu.builder()
                .id(id)
                .store(store)
                .name("Test Menu " + id)
                .price(10000L)
                .build();
    }

    private Cart createCart(User user, Store store) {
        return Cart.builder()
                .user(user)
                .store(store)
                .build();
    }

    private CartMenu createCartMenu(Cart cart, Menu menu, long quantity) {
        return CartMenu.builder()
                .cart(cart)
                .menu(menu)
                .quantity(quantity)
                .build();
    }


}

