package com.my.foody.domain.order.service;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.address.service.AddressService;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest extends DummyObject {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private UserService userService;

    @Mock
    private StoreService storeService;

    @Mock
    private CartMenuRepository cartMenuRepository;

    private Order order;
    private Owner owner;
    private Store store;
    private User user;
    private Address address;
    private Cart cart;
    private Menu menu;

    @BeforeEach
    public void setUp() {
        owner = mockOwner(1L);
        store = mockStore(owner);
        user = newUser(1L);
        address = mockAddress(user);
        order = mockOrder(owner, user, address);

        menu = Menu.builder()
                .price(100L)
                .name("테스트 메뉴")
                .build();

        cart = Cart.builder()
                .store(store)
                .build();
    }

    @Test
    @DisplayName("주문 상태 변경 성공 테스트")
    void updateOrderStatus_Success() {

        Long orderId = 1L;
        Long ownerId = 1L;

        // 빌더 사용
        OrderStatusUpdateReqDto requestDto = OrderStatusUpdateReqDto.builder()
                .orderStatus(OrderStatus.ACCEPTED)
                .build();

        // Mock 설정
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatusUpdateRespDto responseDto = orderService.updateOrderStatus(requestDto, orderId, ownerId);

        // 변경된 주문 상태 출력
        System.out.println("변경된 주문 상태: " + responseDto.getOrderStatus());

        assertAll(
                () -> assertNotNull(responseDto),
                () -> assertEquals(OrderStatus.ACCEPTED.name(), responseDto.getOrderStatus())
        );

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }


    @Test
    @DisplayName(value = "주문 상태 변경실패 테스트 권한없음")
    void updateFailure() {
        Long orderId = 1L;  // 테스트할 주문 ID
        Long ownerId = 2L;  // 실제 주문의 가게 주인 ID와 다른 가게 주인 ID

        OrderStatusUpdateReqDto requestDto = OrderStatusUpdateReqDto.builder()
                .orderStatus(OrderStatus.ACCEPTED)
                .build();

        // Mock 설정: 주문이 존재하지만, 주인의 ID가 일치하지 않음
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try {
            // 예외 발생 검증
            orderService.updateOrderStatus(requestDto, orderId, ownerId);
            System.out.println("테스트 실패: 예외가 발생하지 않았습니다."); // 예외가 발생하지 않을 경우 메시지 출력
        } catch (BusinessException exception) {
            // 예외 메시지 검증
            assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());

            System.out.println("테스트 성공:" + exception.getErrorCode() + " 예외가 발생했습니다."); // 예외 발생 시 메시지 출력
        }

        // 주문 저장 메서드는 호출되지 않아야 함
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 미리보기 성공 테스트")
    void getOrderPreview_Success_test() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;

        User user = mock(User.class);
        Store store = mock(Store.class);
        Cart cart = mock(Cart.class);
        Address address = mock(Address.class);
        Menu menu = mock(Menu.class);

        // Menu mock 설정
        when(menu.getPrice()).thenReturn(10000L);
        when(menu.getName()).thenReturn("테스트메뉴");

        CartMenu cartMenu = mock(CartMenu.class);
        when(cartMenu.getMenu()).thenReturn(menu);
        when(cartMenu.getQuantity()).thenReturn(2L);

        List<CartMenu> cartMenuList = List.of(cartMenu);


        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(cartRepository.findByIdAndUser(cartId, user)).thenReturn(Optional.of(cart));
        when(addressService.findMainAddress(userId)).thenReturn(address);
        when(cartMenuRepository.findByCartWithMenu(cart)).thenReturn(cartMenuList);

        // when
        OrderPreviewRespDto result = orderService.getOrderPreview(userId, storeId, cartId);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(cartRepository).findByIdAndUser(cartId, user);
        verify(addressService).findMainAddress(userId);
        verify(cartMenuRepository).findByCartWithMenu(cart);
    }

    @Test
    @DisplayName("주문 미리보기 실패 테스트: 존재하지 않는 사용자")
    void getOrderPreview_UserNotFound() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;

        when(userService.findActivateUserByIdOrFail(userId))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() ->
                orderService.getOrderPreview(userId, storeId, cartId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService, never()).findActivateStoreByIdOrFail(anyLong());
        verify(cartRepository, never()).findByIdAndUser(anyLong(), any());
        verify(addressService, never()).findMainAddress(anyLong());
        verify(cartMenuRepository, never()).findByCartWithMenu(any());
    }

    @Test
    @DisplayName("주문 미리보기 실패 테스트: 빈 장바구니")
    void getOrderPreview_EmptyCart() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;

        User user = mock(User.class);
        Store store = mock(Store.class);
        Cart cart = mock(Cart.class);
        Address address = mock(Address.class);

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(cartRepository.findByIdAndUser(cartId, user)).thenReturn(Optional.of(cart));
        when(addressService.findMainAddress(userId)).thenReturn(address);
        when(cartMenuRepository.findByCartWithMenu(cart)).thenReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() ->
                orderService.getOrderPreview(userId, storeId, cartId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CART_IS_EMPTY);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(cartRepository).findByIdAndUser(cartId, user);
        verify(addressService).findMainAddress(userId);
        verify(cartMenuRepository).findByCartWithMenu(cart);
    }

    @Test
    @DisplayName("주문 미리보기 실패 테스트: 품절된 메뉴 포함")
    void getOrderPreview_SoldOutMenu() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;

        User user = mock(User.class);
        Store store = mock(Store.class);
        Cart cart = mock(Cart.class);
        Address address = mock(Address.class);

        CartMenu cartMenu = mock(CartMenu.class);
        doThrow(new BusinessException(ErrorCode.MENU_NOT_AVAILABLE))
                .when(cartMenu).validateMenuCanOrder();

        List<CartMenu> cartMenuList = List.of(cartMenu);

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(storeService.findActivateStoreByIdOrFail(storeId)).thenReturn(store);
        when(cartRepository.findByIdAndUser(cartId, user)).thenReturn(Optional.of(cart));
        when(addressService.findMainAddress(userId)).thenReturn(address);
        when(cartMenuRepository.findByCartWithMenu(cart)).thenReturn(cartMenuList);

        // when & then
        assertThatThrownBy(() ->
                orderService.getOrderPreview(userId, storeId, cartId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MENU_NOT_AVAILABLE);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(storeService).findActivateStoreByIdOrFail(storeId);
        verify(cartRepository).findByIdAndUser(cartId, user);
        verify(addressService).findMainAddress(userId);
        verify(cartMenuRepository).findByCartWithMenu(cart);
    }

}
