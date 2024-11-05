package com.my.foody.domain.order.service;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.entity.Store;
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

import java.util.Optional;

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
                .quantity(2L)
                .store(store)
                .menu(menu)
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

        //given
        Long userId = user.getId();
        Long storeId = store.getId();
        Long cartId = cart.getId();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndIsMain(userId,true)).thenReturn(Optional.of(address));
        when(cartRepository.findWithStoreAndMenuByIdAndUserIdAndStoreId(cartId, userId, storeId))
                .thenReturn(Optional.of(cart));

        //then
        OrderPreviewRespDto orderPreview = orderService.getOrderPreview(userId, storeId, cartId);

        assertNotNull(orderPreview);
        assertEquals(address.getRoadAddress(), orderPreview.getRoadAddress());
        assertEquals(address.getDetailedAddress(), orderPreview.getDetailedAddress());
        assertEquals(user.getContact(), orderPreview.getUserContact());
        assertEquals(store.getName(), orderPreview.getStoreName());
        assertEquals(store.getId(), orderPreview.getStoreId());
        assertEquals(menu.getName(), orderPreview.getMenuName());
        assertEquals(menu.getPrice(), orderPreview.getMenuPrice());
        assertEquals(cart.getQuantity(), orderPreview.getQuantity());
        assertEquals(menu.getPrice() * cart.getQuantity(), orderPreview.getTotalAmount());

        // Verifying the interactions with the mocks
        verify(userRepository).findById(userId);
        verify(addressRepository).findByUserIdAndIsMain(userId, true);
        verify(cartRepository).findWithStoreAndMenuByIdAndUserIdAndStoreId(cartId, userId, storeId);
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
}
