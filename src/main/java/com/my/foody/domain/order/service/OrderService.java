package com.my.foody.domain.order.service;

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
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.orderMenu.entity.OrderMenu;
import com.my.foody.domain.orderMenu.repo.OrderMenuRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CartMenuRepository cartMenuRepository;
    private final OrderMenuRepository orderMenuRepository;

    private  final UserService userService;
    private final AddressService addressService;
    private final StoreService storeService;
    private final MenuService menuService;


    public OrderStatusUpdateRespDto updateOrderStatus(OrderStatusUpdateReqDto requestDto, Long orderId, Long ownerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 가게 주인 맞는지 확인
        order.isStoreOwner(ownerId);

        // 주문 상태 업데이트
        order.updateOrderStatus(requestDto.getOrderStatus());

        // 변경된 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 응답 DTO 생성
        return new OrderStatusUpdateRespDto(savedOrder.getOrderStatus().name());
    }

    //TODO 이거 수정해야 함!!
    public OrderPreviewRespDto getOrderPreview(Long userId, Long storeId, Long cartId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Address address = addressRepository.findByUserIdAndIsMain(userId, true)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        Cart cart = cartRepository.findWithStoreAndMenuByIdAndUserIdAndStoreId(cartId, userId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        return null;

//        Store store = cart.getStore();
//        Menu menu = cart.getMenu();
//
//        Long totalAmount = menu.getPrice() * cart.getQuantity();
//
//        return OrderPreviewRespDto.builder()
//                .roadAddress(address.getRoadAddress())
//                .detailedAddress(address.getDetailedAddress())
//                .userContact(user.getContact())
//                .storeName(store.getName())
//                .storeId(store.getId())
//                .menuName(menu.getName())
//                .menuPrice(menu.getPrice())
//                .quantity(cart.getQuantity())
//                .totalAmount(totalAmount)
//                .build();
    }

    public void createOrder(Long storeId, Long cartId, OrderCreateReqDto orderCreateReqDto, Long userId) {

        User user = userService.findActivateUserByIdOrFail(userId);
        Store store = storeService.findActivateStoreByIdOrFail(storeId);
        addressService.findByIdOrFail(orderCreateReqDto.getUserAddressId());


        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        if (!cart.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }

        if (orderCreateReqDto.getPaymentAmount() < store.getMinOrderAmount()) {
            throw new BusinessException(ErrorCode.UNDER_MINIMUM_ORDER_AMOUNT);
        }

        LocalTime now = LocalTime.now();
        if (now.isBefore(store.getOpenTime()) || now.isAfter(store.getEndTime())) {
            throw new BusinessException(ErrorCode.STORE_CLOSED);
        }

        Order order = Order.builder()
                .user(user)
                .store(store)
                .address(addressService.findByIdOrFail(orderCreateReqDto.getUserAddressId()))
                .totalAmount(orderCreateReqDto.getPaymentAmount())
                .build();
        orderRepository.save(order);

        List<CartMenu> cartMenus = cartMenuRepository.findByCart(cart);
        for (CartMenu cartMenu : cartMenus) {
            OrderMenu orderMenu = OrderMenu.builder()
                    .order(order)
                    .menuId(cartMenu.getMenu().getId())
                    .quantity(cartMenu.getQuantity())
                    .price(cartMenu.getMenu().getPrice())
                    .build();
            orderMenuRepository.save(orderMenu);
        }
    }

}
