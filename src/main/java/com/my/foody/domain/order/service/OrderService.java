package com.my.foody.domain.order.service;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cart.repo.CartRepository;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderListRespDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.order.repo.dto.OrderProjectionRespDto;
import com.my.foody.domain.orderMenu.repo.OrderMenuRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.service.OwnerService;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
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
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OwnerService ownerService;
    private final StoreRepository storeRepository;
    private final OrderMenuRepository orderMenuRepository;

    @Transactional
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
        // Fetch the user
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

    public OrderListRespDto getAllOrder(Long ownerId, int page, int limit) {
        Owner owner = ownerService.findActivateOwnerByIdOrFail(ownerId);
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderProjectionRespDto> orderPage
                = orderMenuRepository.findByOwnerWithOrderWithStoreWithMenu(owner, pageable);
        return new OrderListRespDto(orderPage);
    }
}
