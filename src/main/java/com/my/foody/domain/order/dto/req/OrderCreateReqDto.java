package com.my.foody.domain.order.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateReqDto {
    private Long addressId;
    private Long userId;
    private Long totalAmount;
    private Long menuId;
}
