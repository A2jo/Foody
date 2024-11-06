package com.my.foody.domain.order.service;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.domain.order.dto.req.OrderCreateReqDto;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.orderMenu.entity.OrderMenu;
import com.my.foody.domain.orderMenu.repo.OrderMenuRepository;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
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

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest extends DummyObject {

    @InjectMocks
    private OrderService orderService;

    @InjectMocks
    private MenuService menuService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OrderMenuRepository orderMenuRepository;

    @Mock
    private CartMenuRepository cartMenuRepository;

    @Mock
    private MenuRepository menuRepository;

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
    void getOrderPreview_Success() {
        Long userId = user.getId();
        Long storeId = store.getId();
        Long cartId = cart.getId();

        // Mock setup
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndIsMain(userId, true)).thenReturn(Optional.of(address));
        when(cartRepository.findWithStoreAndMenuByIdAndUserIdAndStoreId(cartId, userId, storeId)).thenReturn(Optional.of(cart));

        menu = Menu.builder()
                .id(1L)
                .name("테스트 메뉴")
                .price(1000L)
                .build();

        when(menuRepository.findActivateMenu(anyLong())).thenReturn(Optional.of(menu));
        when(menuService.findActiveMenuByIdOrFail(menu.getId())).thenReturn(menu);

        OrderPreviewRespDto orderPreview = orderService.getOrderPreview(userId, storeId, cartId);

        // Verifications
        assertNotNull(orderPreview);  // Verify the result is not null
        assertEquals(address.getRoadAddress(), orderPreview.getRoadAddress());
        assertEquals(address.getDetailedAddress(), orderPreview.getDetailedAddress());
        assertEquals(user.getContact(), orderPreview.getUserContact());
        assertEquals(store.getName(), orderPreview.getStoreName());
        assertEquals(store.getId(), orderPreview.getStoreId());
        assertEquals(menu.getName(), orderPreview.getMenuName());
        assertEquals(menu.getPrice(), orderPreview.getMenuPrice());

        // Verify interactions with mocks
        verify(userRepository).findById(userId);
        verify(addressRepository).findByUserIdAndIsMain(userId, true);
        verify(cartRepository).findWithStoreAndMenuByIdAndUserIdAndStoreId(cartId, userId, storeId);
        verify(menuRepository).findActivateMenu(anyLong());
    }


    @Test
    @DisplayName("주문 미리보기 실패 테스트 - 사용자 미발견")
    void getOrderPreview_UserNotFound() {
        Long userId = 1L;
        Long storeId = 1L;
        Long cartId = 1L;

        // Mocking behavior for dependencies
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Exception verification
        assertThrows(BusinessException.class, () -> {
            orderService.getOrderPreview(userId, storeId, cartId);
        }, " 예상된 USER_NOT_FOUND의 예외 처리");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("주문 미리보기 실패 테스트 - 주소 미발견")
    void getOrderPreview_AddressNotFound() {
        Long userId = user.getId();
        Long storeId = store.getId();
        Long cartId = cart.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndIsMain(userId, true)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            orderService.getOrderPreview(userId, storeId, cartId);
        }, "예상된 ADDRESS_NOT_FOUND의 예외 처리");

        verify(userRepository).findById(userId);
        verify(addressRepository).findByUserIdAndIsMain(userId, true);
    }

    @Test
    @DisplayName("주문 미리보기 실패 테스트 - 카트 항목 미발견")
    void getOrderPreview_CartItemNotFound() {

        //given
        Long userId = user.getId();
        Long storeId = store.getId();
        Long cartId = cart.getId();

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndIsMain(userId, true)).thenReturn(Optional.of(address));
        when(cartRepository.findWithStoreAndMenuByIdAndUserIdAndStoreId(cartId, userId, storeId))
                .thenReturn(Optional.empty());

       //then
        assertThrows(BusinessException.class, () -> {
            orderService.getOrderPreview(userId, storeId, cartId);
        }, "예상된 CART_ITEM_NOT_FOUND의 예외 처리");

        verify(userRepository).findById(userId);
        verify(addressRepository).findByUserIdAndIsMain(userId, true);
        verify(cartRepository).findWithStoreAndMenuByIdAndUserIdAndStoreId(cartId, userId, storeId);
    }

    @Test
    @DisplayName("주문 생성 성공 테스트")
    void createOrder_Success() {
        // given
        Long storeId = store.getId();
        Long cartId = cart.getId();
        Long userId = user.getId();

        OrderCreateReqDto orderCreateReqDto = OrderCreateReqDto.builder()
                .userAddressId(address.getId())
                .totalAmount(200L)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(addressRepository.findById(orderCreateReqDto.getUserAddressId())).thenReturn(Optional.of(address));

        CartMenu cartMenu = CartMenu.builder()
                .menu(menu)
                .quantity(2L)
                .build();
        when(cartMenuRepository.findByCart(cart)).thenReturn(List.of(cartMenu));


        when(menuService.findActiveMenuByIdOrFail(menu.getId())).thenReturn(menu);

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMenuRepository.save(any(OrderMenu.class))).thenReturn(OrderMenu.builder().build());

        // when
        orderService.createOrder(storeId, cartId, orderCreateReqDto, userId);

        // then
        verify(userRepository).findById(userId);
        verify(storeRepository).findById(storeId);
        verify(cartRepository).findById(cartId);
        verify(addressRepository).findById(orderCreateReqDto.getUserAddressId());
        verify(cartMenuRepository).findByCart(cart);
        verify(menuService).findActiveMenuByIdOrFail(menu.getId());
        verify(orderRepository).save(any(Order.class));
        verify(orderMenuRepository, times(cartMenu.getQuantity().intValue())).save(any(OrderMenu.class)); // Verifying that OrderMenu was saved correctly

        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(store, order.getStore());
        assertEquals(address, order.getAddress());
        assertEquals(200L, order.getTotalAmount());
    }

    @Test
    @DisplayName("주문 실패 테스트 - 사용자 미발견")
    void createOrder_UserNotFound() {
        Long userId = 1L;
        Long storeId = store.getId();
        Long cartId = cart.getId();
        OrderCreateReqDto orderCreateReqDto = new OrderCreateReqDto(100L, 1L, 150L, 1L); // example values

        // Mocking userRepository to return empty when trying to find the user
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Exception verification
        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(storeId, cartId, orderCreateReqDto, userId);
        }, "예상된 USER_NOT_FOUND의 예외 처리");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("주문 실패 테스트 - 가게 미발견")
    void createOrder_StoreNotFound() {
        Long userId = 1L;
        Long storeId = store.getId();
        Long cartId = cart.getId();
        OrderCreateReqDto orderCreateReqDto = new OrderCreateReqDto(100L, 1L, 150L, 1L);

        // Mocking storeRepository to return empty when trying to find the store
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // Exception verification
        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(storeId, cartId, orderCreateReqDto, userId);
        }, "예상된 STORE_NOT_FOUND의 예외 처리");

        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("주문 실패 테스트 - 카트 항목 미발견")
    void createOrder_CartItemNotFound() {
        Long userId = 1L;
        Long storeId = store.getId();
        Long cartId = cart.getId();
        OrderCreateReqDto orderCreateReqDto = new OrderCreateReqDto(100L, 1L, 150L, 1L);

        // Mocking cartRepository to return empty when trying to find the cart
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Exception verification
        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(storeId, cartId, orderCreateReqDto, userId);
        }, "예상된 CART_ITEM_NOT_FOUND의 예외 처리");

        verify(cartRepository).findById(cartId);
    }

    @Test
    @DisplayName("주문 실패 테스트 - 최소 주문 금액 미달")
    void createOrder_UnderMinimumOrderAmount() {
        Long userId = 1L;
        Long storeId = store.getId();
        Long cartId = cart.getId();
        OrderCreateReqDto orderCreateReqDto = new OrderCreateReqDto(50L, 1L, 150L, 1L); // example value, lower than store's minimum

        // Mock the behavior where the store has a minimum order amount of 100L
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // Exception verification
        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(storeId, cartId, orderCreateReqDto, userId);
        }, "예상된 UNDER_MINIMUM_ORDER_AMOUNT의 예외 처리");

        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("주문 실패 테스트 - 가게 운영시간 외")
    void createOrder_StoreClosed() {
        Long userId = 1L;
        Long storeId = store.getId();
        Long cartId = cart.getId();
        OrderCreateReqDto orderCreateReqDto = new OrderCreateReqDto(100L, 1L, 150L, 1L); // example value

        // Mock store's open and close times, setting current time outside this range
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // Assume current time is outside the store's operating hours (store is closed)
        LocalTime currentTime = LocalTime.of(23, 59); // For example, 11:59 PM
        store.setOpenTime(LocalTime.of(9, 0));  // Open at 9 AM
        store.setEndTime(LocalTime.of(22, 0));  // Close at 10 PM

        // Exception verification
        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(storeId, cartId, orderCreateReqDto, userId);
        }, "예상된 STORE_CLOSED의 예외 처리");

        verify(storeRepository).findById(storeId);
        verify(cartRepository).findById(cartId);
    }
}
