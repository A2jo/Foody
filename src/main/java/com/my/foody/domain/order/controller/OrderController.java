package com.my.foody.domain.order.controller;

import com.my.foody.domain.order.dto.resp.OrderInfoRespDto;
import com.my.foody.domain.order.dto.req.OrderStatusUpdateReqDto;
import com.my.foody.domain.order.dto.resp.OrderListRespDto;
import com.my.foody.domain.order.dto.resp.OrderPreviewRespDto;
import com.my.foody.domain.order.dto.resp.OrderStatusUpdateRespDto;
import com.my.foody.domain.order.service.OrderService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
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
  public ResponseEntity<ApiResult<OrderPreviewRespDto>> getOrderPreview(
          @PathVariable Long storeId,
          @PathVariable Long cartId,
          @CurrentUser TokenSubject tokenSubject) {

    Long userId = tokenSubject.getId();
    OrderPreviewRespDto orderPreview = orderService.getOrderPreview(userId, storeId, cartId);
    ApiResult<OrderPreviewRespDto> apiResult = ApiResult.success(orderPreview);

    return new ResponseEntity<>(apiResult, HttpStatus.OK);
  }

  @RequireAuth(userType = UserType.OWNER)
  @GetMapping("/api/owners/orders")
  public ResponseEntity<ApiResult<OrderListRespDto>> getAllOrder(@RequestParam(value = "page", required = false) @Min(value = 0) int page,
                                                                  @RequestParam(value = "limit", required = false) @Positive int limit,
                                                                  @CurrentUser TokenSubject tokenSubject){
    return new ResponseEntity<>(ApiResult.success(orderService.getAllOrder(tokenSubject.getId(), page, limit)), HttpStatus.OK);
  }


  @RequireAuth(userType = UserType.OWNER)
  @GetMapping("/api/owners/orders/{orderId}")
  public ResponseEntity<ApiResult<OrderInfoRespDto>> getOrderInfo(@RequestParam(value = "orderId", required = true) @Positive Long orderId,
                                                                  @CurrentUser TokenSubject tokenSubject){
    return new ResponseEntity<>(ApiResult.success(orderService.getOrderInfo(tokenSubject.getId(), orderId)), HttpStatus.OK);
  }
}
