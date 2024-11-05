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
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;


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

    public OrderPreviewRespDto getOrderPreview(Long userId, Long storeId, Long cartId) {
        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        Cart cart = cartRepository.findByIdAndUserIdAndStoreId(cartId, userId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        Store store = cart.getStore();
        Menu menu = cart.getMenu();

        Long totalAmount = menu.getPrice() * cart.getQuantity();

        return OrderPreviewRespDto.builder()
                .roadAddress(address.getRoadAddress())
                .detailedAddress(address.getDetailedAddress())
                .userContact(user.getContact())
                .storeName(store.getName())
                .storeId(store.getId())
                .menuName(menu.getName())
                .menuPrice(menu.getPrice())
                .quantity(cart.getQuantity())
                .totalAmount(totalAmount)
                .build();
    }
}
