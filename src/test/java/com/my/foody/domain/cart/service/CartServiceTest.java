package com.my.foody.domain.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import java.util.Arrays;

import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartService cartService;

    private Cart cartItem;
    private User user;


    @BeforeEach
    public void setUp() {

        user = User.builder()
                .id(1L)
                .name("Test user")
                .build();

        Store store = Store.builder()
                .name("Test 음식점")
                .minOrderAmount(5000L)
                .build();

        Menu menu = Menu.builder()
                .name("Sample 메뉴")
                .price(2000L)
                .build();

        cartItem = Cart.builder()
                .store(store)
                .menu(menu)
                .quantity(3L) // 주문 총금액 = 2000 * 3 = 6000
                .build();
    }

    @Test
    public void testGetCartItems() {
        // given
        Long userId = 1L;
        int page = 0;
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());

        Page<Cart> cartItemsPage = new PageImpl<>(Arrays.asList(cartItem));

        //userService의 findById 스텁 추가
        when(userService.findByIdOrFail(userId)).thenReturn(user);
        when(cartRepository.findByUserId(userId, pageable)).thenReturn(cartItemsPage);

        // when
        Page<CartItemRespDto> result = cartService.getCartItems(userId, page, limit);

        // then
        assertEquals(1, result.getTotalElements());
        CartItemRespDto cartDto = result.getContent().get(0);

        assertEquals("Test 음식점", cartDto.getStoreName());
        assertEquals("Sample 메뉴", cartDto.getMenuName());
        assertEquals(2000L, cartDto.getMenuPrice());
        assertEquals(3L, cartDto.getQuantity());
        assertEquals(6000L, cartDto.getTotalOrderAmount());
        assertEquals(5000L, cartDto.getMinOrderAmount());

        verify(cartRepository, times(1)).findByUserId(userId, pageable);
    }
}

