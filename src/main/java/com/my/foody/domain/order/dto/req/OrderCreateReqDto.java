package com.my.foody.domain.order.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
public class OrderCreateReqDto {

    @NotNull
    private Long userAddressId;

    @NotNull
    private Long userId;

    @NotNull(message = "최소 금액 이상 주문이 가능합니다.")
    private Long totalAmount;

    @NotNull
    private Long menuItemId;
}
