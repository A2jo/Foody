package com.my.foody.domain.order.service;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderListRespDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.order.repo.dto.OrderProjectionRespDto;
import com.my.foody.domain.orderMenu.repo.OrderMenuRepository;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.service.OwnerService;
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
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    private OwnerService ownerService;

    @Mock
    private OrderMenuRepository orderMenuRepository;

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

    @Test
    @DisplayName("주문 목록 조회 성공 테스트")
    void getAllOrder_Success() {
        // Given
        Long ownerId = 1L;
        int page = 0;
        int limit = 10;
        Owner owner = newOwner(ownerId);
        Page<OrderProjectionRespDto> mockOrderPage = createMockOrderPage();

        Pageable expectedPageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(ownerService.findActivateOwnerByIdOrFail(ownerId)).thenReturn(owner);
        when(orderMenuRepository.findByOwnerWithOrderWithStoreWithMenu(owner, expectedPageable))
                .thenReturn(mockOrderPage);

        // When
        OrderListRespDto result = orderService.getAllOrder(ownerId, page, limit);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderList()).hasSize(2);
        assertThat(result.getPageInfo().getPageNumber()).isEqualTo(page);
        assertThat(result.getPageInfo().getPageSize()).isEqualTo(limit);

        OrderListRespDto.OrderRespDto firstOrder = result.getOrderList().get(0);
        assertThat(firstOrder.getStoreName()).isEqualTo("맛있는 치킨");
        assertThat(firstOrder.getMenuSummary()).isEqualTo("후라이드 치킨 외 2개");

        verify(ownerService).findActivateOwnerByIdOrFail(ownerId);
        verify(orderMenuRepository).findByOwnerWithOrderWithStoreWithMenu(owner, expectedPageable);
    }


    @Test
    @DisplayName("주문 목록 조회 성공 테스트: 주문이 없는 경우")
    void getAllOrder_EmptyOrders() {
        // Given
        Long ownerId = 1L;
        int page = 0;
        int limit = 10;
        Owner owner = newOwner(ownerId);
        Page<OrderProjectionRespDto> emptyPage = Page.empty(PageRequest.of(page, limit));

        when(ownerService.findActivateOwnerByIdOrFail(ownerId)).thenReturn(owner);
        when(orderMenuRepository.findByOwnerWithOrderWithStoreWithMenu(any(), any())).thenReturn(emptyPage);

        // When
        OrderListRespDto result = orderService.getAllOrder(ownerId, page, limit);

        // Then
        assertThat(result.getOrderList()).isEmpty();
        assertThat(result.getPageInfo().getTotalElements()).isZero();
    }

    @Test
    @DisplayName("주문 목록 조회 실패 테스트: 존재하지 않는 사장님")
    void getAllOrder_OwnerNotFound() {
        // Given
        Long nonExistentOwnerId = 999L;
        when(ownerService.findActivateOwnerByIdOrFail(nonExistentOwnerId))
                .thenThrow(new BusinessException(ErrorCode.OWNER_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() ->
                orderService.getAllOrder(nonExistentOwnerId, 0, 10)
        ).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.OWNER_NOT_FOUND.getMsg());
    }


    @Test
    @DisplayName("주문 목록 조회 성공 테스트: 메뉴명 포맷팅 확인")
    void getAllOrder_MenuNameFormatting() {
        Long ownerId = 1L;
        int page = 0;
        int limit = 10;
        Owner owner = newOwner(ownerId);
        Page<OrderProjectionRespDto> mockOrderPage = createMockOrderPage();

        when(ownerService.findActivateOwnerByIdOrFail(ownerId)).thenReturn(owner);
        when(orderMenuRepository.findByOwnerWithOrderWithStoreWithMenu(any(), any())).thenReturn(mockOrderPage);

        // When
        OrderListRespDto result = orderService.getAllOrder(ownerId, page, limit);

        // Then
        OrderListRespDto.OrderRespDto firstOrder = result.getOrderList().get(0);
        assertThat(firstOrder.getMenuSummary())
                .isEqualTo("후라이드 치킨 외 2개");

        OrderListRespDto.OrderRespDto secondOrder = result.getOrderList().get(1);
        assertThat(secondOrder.getMenuSummary())
                .isEqualTo("페퍼로니피자");
    }


    private Page<OrderProjectionRespDto> createMockOrderPage() {
        List<OrderProjectionRespDto> orders = Arrays.asList(
                createMockOrderProjection("맛있는 치킨", "후라이드 치킨,양념 치킨,콜라"),
                createMockOrderProjection("맛있는 피자", "페퍼로니피자")
        );

        return new PageImpl<>(
                orders,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                orders.size()
        );
    }

    private OrderProjectionRespDto createMockOrderProjection(String storeName, String menuNames) {
        return new OrderProjectionRespDto() {
            @Override
            public String getStoreName() {
                return storeName;
            }

            @Override
            public OrderStatus getOrderStatus() {
                return OrderStatus.DELIVERED;
            }

            @Override
            public String getRoadAddress() {
                return "서울시 강남구";
            }

            @Override
            public String getDetailedAddress() {
                return "123번길";
            }

            @Override
            public String getMenuNames() {
                return menuNames;
            }

            @Override
            public Long getOrderId() {
                return 1L;
            }

            @Override
            public Long getTotalAmount() {
                return 50000L;
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return LocalDateTime.now();
            }
        };
    }



}
