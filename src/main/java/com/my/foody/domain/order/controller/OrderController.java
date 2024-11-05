package com.my.foody.domain.order.controller;

import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.service.OrderService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PutMapping("api/owners/orders/{orderId}")
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<OrderStatusUpdateRespDto> changeOrderStatus(@PathVariable("orderId") Long orderId,
                                                                      @RequestBody OrderStatusUpdateReqDto requestDto,
                                                                      @CurrentUser TokenSubject tokenSubject) {
        // ownerId
        Long ownerId = tokenSubject.getId();

        // 주문 상태 업데이트 및 응답 DTO 반환
        OrderStatusUpdateRespDto responseDto = orderService.updateOrderStatus(requestDto, orderId, ownerId);

        return ResponseEntity.ok(responseDto);
    }
}
