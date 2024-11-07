package com.my.foody.domain.order.service;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.address.service.AddressService;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderInfoRespDto;
import com.my.foody.domain.order.dto.resp.OrderListRespDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.order.service.timepro.TimeProvider;
import com.my.foody.domain.orderMenu.entity.OrderMenu;
import com.my.foody.domain.orderMenu.repo.dto.OrderMenuProjectionDto;
import com.my.foody.domain.orderMenu.repo.dto.OrderProjectionDto;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.orderMenu.repo.OrderMenuRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.service.OwnerService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;

import java.time.LocalTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressService addressService;
    private final UserService userService;
    private final StoreService storeService;
    private final CartMenuRepository cartMenuRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final AddressRepository addressRepository;

    private final MenuService menuService;
    private final OwnerService ownerService;
    private final TimeProvider timeProvider;

    @Transactional
    public OrderStatusUpdateRespDto updateOrderStatus(OrderStatusUpdateReqDto requestDto, Long orderId, Long ownerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 가게 주인 맞는지 확인
        isStoreOwner(order, ownerId);

        // 주문 상태 업데이트
        order.updateOrderStatus(OrderStatus.valueOf(requestDto.getOrderStatus()));

        // 변경된 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 응답 DTO 생성
        return new OrderStatusUpdateRespDto(savedOrder.getOrderStatus().name());
    }


    public OrderPreviewRespDto getOrderPreview(Long userId, Long storeId, Long cartId) {

        User user = userService.findActivateUserByIdOrFail(userId);
        Store store = storeService.findActivateStoreByIdOrFail(storeId);
        Cart cart = cartRepository.findByIdAndUser(cartId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        //기본 주소지 찾기
        Address address = addressService.findMainAddress(userId);

        //장바구니에 담은 메뉴가 soldOut 되었거나 삭제되었는지 검증
        List<CartMenu> cartMenuList = cartMenuRepository.findByCartWithMenu(cart);

        //장바구니가 비어있는지 검사
        if (cartMenuList.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_IS_EMPTY);
        }
        //품절 되거나 삭제된 메뉴가 포함되어 있는지 검사
        validateCartMenus(cartMenuList);

        //전체 주문 금액 계산
        Long totalAmount = calculateTotalAmount(cartMenuList);
        return new OrderPreviewRespDto(user, store, cart, cartMenuList, address, totalAmount);
    }

    private void validateCartMenus(List<CartMenu> cartMenuList) {
        cartMenuList.forEach(CartMenu::validateMenuCanOrder);
    }

    private Long calculateTotalAmount(List<CartMenu> cartMenuList) {
        return cartMenuList.stream()
                .mapToLong(cartMenu -> cartMenu.getMenu().getPrice() * cartMenu.getQuantity())
                .sum();
    }

    @Transactional
    public void createOrder(Long cartId,  Long userId) {

        User user = userService.findActivateUserByIdOrFail(userId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        Store store = storeService.findActivateStoreByIdOrFail(cart.getStore().getId());

        Address mainAddress = addressRepository.findByUserIdAndIsMain(userId, true)
                .orElseThrow(() -> new BusinessException(ErrorCode.MAIN_ADDRESS_NOT_FOUND));

        List<CartMenu> cartMenuList = cartMenuRepository.findByCart(cart);

        long totalPrice = 0;

        for (CartMenu cartMenu : cartMenuList) {
            totalPrice += (cartMenu.getQuantity() * cartMenu.getMenu().getPrice());
        }

        if (totalPrice < store.getMinOrderAmount()) {
            throw new BusinessException(ErrorCode.UNDER_MINIMUM_ORDER_AMOUNT);
        }

        LocalTime now = timeProvider.now();

        if (now.isBefore(store.getOpenTime()) || now.isAfter(store.getEndTime())) {
            throw new BusinessException(ErrorCode.STORE_CLOSED);
        }

        Order order = Order.builder()
                .user(user)
                .store(store)
                .address(mainAddress)
                .totalAmount(totalPrice)
                .orderStatus(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        for (CartMenu cartMenu : cartMenuList) {
            Menu menu = cartMenu.getMenu();
            OrderMenu orderMenu = OrderMenu.builder()
                    .order(order)
                    .menuId(menu.getId())
                    .quantity(cartMenu.getQuantity())
                    .price(menu.getPrice())
                    .build();
            orderMenuRepository.save(orderMenu);
        }
    }

    public OrderListRespDto getAllOrder(Long ownerId, int page, int limit) {
        Owner owner = ownerService.findActivateOwnerByIdOrFail(ownerId);
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderProjectionDto> orderPage
                = orderMenuRepository.findByOwnerWithOrderWithStoreWithMenu(owner, pageable);
        return new OrderListRespDto(orderPage);
    }

    public OrderInfoRespDto getOrderInfo(Long ownerId, Long orderId) {
        ownerService.findActivateOwnerByIdOrFail(ownerId);
        Order order = orderRepository.findOrderWithDetails(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        List<OrderMenuProjectionDto> orderDetailProjection = orderMenuRepository.findOrderMenuDetailByOrder(order);
        return new OrderInfoRespDto(orderDetailProjection, order);
    }

    // 가게주인 확인 메소드
    private void isStoreOwner(Order order, Long ownerId) {
        if (!order.getStore().getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

    }
}
