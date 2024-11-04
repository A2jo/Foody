package com.my.foody.domain.order.service;

import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public ResponseEntity<OrderStatusUpdateRespDto> updateOrderStatus(OrderStatusUpdateReqDto requestDto, Long orderId, Long ownerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        
        // 가게 주인 맞는지 확인
        if(!order.getStore().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 주문 상태 업데이트
        order.updateOrderStatus(requestDto.getOrderStatus());

        // 변경된 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 응답 DTO 생성
        OrderStatusUpdateRespDto responseDto = new OrderStatusUpdateRespDto(savedOrder.getOrderStatus().name());

        // ResponseEntity로 반환
        return ResponseEntity.ok(responseDto);
    }
}
