package com.my.foody.domain.cart.service;

import com.my.foody.domain.cart.dto.req.CartMenuCreateReqDto;
import com.my.foody.domain.cart.dto.resp.CartItemListRespDto;
import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.dto.resp.CartMenuCreateRespDto;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.cartMenu.dto.CartMenuDetailProjectionDto;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final StoreService storeService;
    private final MenuService menuService;
    private final CartMenuRepository cartMenuRepository;

    public CartItemListRespDto getCartItems(Long userId, int page, int limit) {
        User user = userService.findActivateUserByIdOrFail(userId);
        //유저의 장바구니 조회
        Cart cart = cartRepository.findByUserWithStore(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CartMenuDetailProjectionDto> cartMenuDetailPage = cartMenuRepository.findCartMenuDetailByCart(cart, pageable);
        return new CartItemListRespDto(cartMenuDetailPage, cart);
    }

    @Transactional
    public CartMenuCreateRespDto addCartItem(Long storeId, Long menuId, CartMenuCreateReqDto cartMenuCreateReqDto, Long userId) {
        User user = userService.findActivateUserByIdOrFail(userId);
        Store store = storeService.findActivateStoreByIdOrFail(storeId);
        Menu menu = menuService.findActiveMenuByIdOrFail(menuId);

        //메뉴와 가게가 일치하는지 검증
        validateMenuBelongsToStore(menu, store);

        //장바구니 조회 또는 생성
        Cart cart = getOrCreateCart(user, store);

        //다른 가게 메뉴 담기 시도 시 처리 -> 장바구니 초기화 및 가게 변경
        handleDifferentStoreMenu(cart, menu.getStore());

        //장바구니 메뉴 추가
        CartMenu cartMenu = CartMenu.builder()
                .cart(cart)
                .menu(menu)
                .quantity(cartMenuCreateReqDto.getQuantity()).build();

        cartMenuRepository.save(cartMenu);
        return new CartMenuCreateRespDto(cartMenu);
    }

    private void handleDifferentStoreMenu(Cart cart, Store newStore) {
        if (!cart.getStore().equals(newStore)) {
            log.info("유저 장바구니 초기화- 유저 ID: {} - 가게 ID: {} 에서 새로운 가게 ID: {}로 전환",
                    cart.getUser().getId(), cart.getStore().getId(), newStore.getId());
            cartMenuRepository.deleteByCart(cart);
            cart.changeStore(newStore);
        }
    }

    private Cart getOrCreateCart(User user, Store initialStore) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .store(initialStore)
                                .user(user)
                                .build()
                ));
    }

    private void validateMenuBelongsToStore(Menu menu, Store store) {
        if (!menu.getStore().getId().equals(store.getId())) {
            throw new BusinessException(ErrorCode.MENU_STORE_MISMATCH);
        }
    }

}
