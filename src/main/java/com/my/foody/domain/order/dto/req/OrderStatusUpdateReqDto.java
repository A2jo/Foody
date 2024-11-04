package com.my.foody.domain.order.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OrderStatusUpdateReqDto {
    @NotBlank(message = "주문상태를 입력해 주세요")
    private String orderStatus;
}
