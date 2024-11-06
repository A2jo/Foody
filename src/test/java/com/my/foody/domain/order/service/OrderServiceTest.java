package com.my.foody.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.address.service.AddressService;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.domain.order.dto.req.OrderCreateReqDto;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderInfoRespDto;
import com.my.foody.domain.order.dto.resp.OrderListRespDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.orderMenu.entity.OrderMenu;
import com.my.foody.domain.orderMenu.repo.OrderMenuRepository;
import com.my.foody.domain.orderMenu.repo.dto.OrderMenuProjectionDto;
import com.my.foody.domain.orderMenu.repo.dto.OrderProjectionDto;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.service.OwnerService;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.util.DummyObject;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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
    private AddressService addressService;

    @Mock
    private UserService userService;

    @Mock
    private StoreService storeService;

    @Mock
    private CartMenuRepository cartMenuRepository;

    @Mock
    private OwnerService ownerService;
    private StoreRepository storeRepository;

    @Mock
    private OrderMenuRepository orderMenuRepository;

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

    @Test
    @DisplayName("주문 목록 조회 성공 테스트")
    void getAllOrder_Success() {
        // Given
        Long ownerId = 1L;
        int page = 0;
        int limit = 10;
        Owner owner = newOwner(ownerId);
        Page<OrderProjectionDto> mockOrderPage = createMockOrderPage();

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
        Page<OrderProjectionDto> emptyPage = Page.empty(PageRequest.of(page, limit));

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
        Page<OrderProjectionDto> mockOrderPage = createMockOrderPage();

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


    @Test
    @DisplayName("주문 상세 정보 조회 성공 테스트")
    void getOrderInfo_Success() {
        // Given
        Long ownerId = 1L;
        Long orderId = 1L;
        Owner owner = newOwner(ownerId);
        Order order = createOrder();
        List<OrderMenuProjectionDto> orderMenus = createOrderMenuProjections();

        when(ownerService.findActivateOwnerByIdOrFail(ownerId)).thenReturn(owner);
        when(orderRepository.findOrderWithDetails(orderId)).thenReturn(Optional.of(order));
        when(orderMenuRepository.findOrderMenuDetailByOrder(order)).thenReturn(orderMenus);

        // When
        OrderInfoRespDto result = orderService.getOrderInfo(ownerId, orderId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(order.getId());
        assertThat(result.getStoreName()).isEqualTo(order.getStore().getName());
        assertThat(result.getTotalAmount()).isEqualTo(order.getTotalAmount());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.DELIVERED.getDescription());
        assertThat(result.getUserContact()).isEqualTo(order.getUser().getContact());
        assertThat(result.getRoadAddress()).isEqualTo(order.getAddress().getRoadAddress());
        assertThat(result.getDetailedAddress()).isEqualTo(order.getAddress().getDetailedAddress());

        // OrderMenu 검증
        List<OrderInfoRespDto.OrderInfoDto> orderList = result.getOrderList();
        assertThat(orderList).hasSize(2);
        assertThat(orderList.get(0).getMenuName()).isEqualTo("후라이드 치킨");
        assertThat(orderList.get(0).getQuantity()).isEqualTo(2L);
        assertThat(orderList.get(0).getAmount()).isEqualTo(18000L);

        verify(ownerService).findActivateOwnerByIdOrFail(ownerId);
        verify(orderRepository).findOrderWithDetails(orderId);
        verify(orderMenuRepository).findOrderMenuDetailByOrder(order);
    }


    @Test
    @DisplayName("주문 상세 조회 실패 테스트: 존재하지 않는 주문")
    void getOrderInfo_OrderNotFound() {
        // Given
        Long ownerId = 1L;
        Long orderId = 999L;
        Owner owner = newOwner(orderId);

        when(ownerService.findActivateOwnerByIdOrFail(ownerId)).thenReturn(owner);
        when(orderRepository.findOrderWithDetails(orderId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderInfo(ownerId, orderId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND);

        verify(ownerService).findActivateOwnerByIdOrFail(ownerId);
        verify(orderRepository).findOrderWithDetails(orderId);
        verify(orderMenuRepository, never()).findOrderMenuDetailByOrder(any());
    }

    @Test
    @DisplayName("주문 상세 조회 실패 테스트: 비활성화된 사장님")
    void getOrderInfo_InactiveOwner() {
        // Given
        Long ownerId = 1L;
        Long orderId = 1L;

        when(ownerService.findActivateOwnerByIdOrFail(ownerId))
                .thenThrow(new BusinessException(ErrorCode.OWNER_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderInfo(ownerId, orderId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OWNER_NOT_FOUND);

        verify(ownerService).findActivateOwnerByIdOrFail(ownerId);
        verify(orderRepository, never()).findOrderWithDetails(anyLong());
        verify(orderMenuRepository, never()).findOrderMenuDetailByOrder(any());
    }


    private Order createOrder() {
        Store store = Store.builder()
                .id(1L)
                .name("맛있는 치킨")
                .build();

        User user = User.builder()
                .id(1L)
                .contact("010-1234-5678")
                .build();

        Address address = Address.builder()
                .roadAddress("서울시 강남구")
                .detailedAddress("테헤란로 123")
                .build();

        return Order.builder()
                .id(1L)
                .store(store)
                .user(user)
                .address(address)
                .totalAmount(50000L)
                .orderStatus(OrderStatus.DELIVERED)
                .build();
    }

    private List<OrderMenuProjectionDto> createOrderMenuProjections() {
        return List.of(
                new OrderMenuProjectionDto() {
                    @Override
                    public Long getQuantity() { return 2L; }
                    @Override
                    public Long getPrice() { return 18000L; }
                    @Override
                    public Long getMenuId() { return 1L; }
                    @Override
                    public String getMenuName() { return "후라이드 치킨"; }
                },
                new OrderMenuProjectionDto() {
                    @Override
                    public Long getQuantity() { return 1L; }
                    @Override
                    public Long getPrice() { return 19000L; }
                    @Override
                    public Long getMenuId() { return 2L; }
                    @Override
                    public String getMenuName() { return "양념 치킨"; }
                }
        );
    }


    private Page<OrderProjectionDto> createMockOrderPage() {
        List<OrderProjectionDto> orders = Arrays.asList(
                createMockOrderProjection("맛있는 치킨", "후라이드 치킨,양념 치킨,콜라"),
                createMockOrderProjection("맛있는 피자", "페퍼로니피자")
        );

        return new PageImpl<>(
                orders,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                orders.size()
        );
    }

    private OrderProjectionDto createMockOrderProjection(String storeName, String menuNames) {
        return new OrderProjectionDto() {
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
