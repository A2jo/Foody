package com.my.foody.domain.order.controller;

import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.service.OrderService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PutMapping("api/owners/orders/{orderId}")
  @RequireAuth(userType = UserType.OWNER)
  public ResponseEntity<ApiResult<OrderStatusUpdateRespDto>> changeOrderStatus(
      @PathVariable("orderId") Long orderId,
      @RequestBody OrderStatusUpdateReqDto requestDto,
      @CurrentUser TokenSubject tokenSubject) {

    Long ownerId = tokenSubject.getId();

    // 주문 상태 업데이트 및 응답 DTO 반환
    OrderStatusUpdateRespDto responseDto = orderService.updateOrderStatus(requestDto, orderId, ownerId);
    ApiResult<OrderStatusUpdateRespDto> apiResult = ApiResult.success(responseDto);

    return new ResponseEntity<>(apiResult, HttpStatus.OK);
  }

  @GetMapping("/api/home/stores/{storeId}/cart/{cartId}/orders")
  @RequireAuth(userType = UserType.USER)
  public ResponseEntity<ApiResult<OrderPreviewRespDto>> getOrderPreview(@PathVariable(value = "storeId", required = true) Long storeId,
                                                                        @PathVariable(value = "cartId", required = true) Long cartId,
                                                                        @CurrentUser TokenSubject tokenSubject) {
    return new ResponseEntity<>(ApiResult.success(orderService.getOrderPreview(tokenSubject.getId(), storeId, cartId)), HttpStatus.OK);
  }
}
