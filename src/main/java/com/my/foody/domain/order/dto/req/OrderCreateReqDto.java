package com.my.foody.domain.order.dto.req;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateReqDto {
    private Long addressId;
    private Long userId;
    private Long totalAmount;
    private Long menuId;
}
