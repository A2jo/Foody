package com.my.foody.domain.order.dto.req;

import com.my.foody.domain.owner.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderStatusUpdateReqDto {
    @NotNull(message = "주문상태를 입력해 주세요")
    private OrderStatus orderStatus;
}
