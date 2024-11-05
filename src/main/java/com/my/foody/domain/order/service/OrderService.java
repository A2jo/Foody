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

import java.util.List;

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
        if(cartMenuList.isEmpty()){
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
}
