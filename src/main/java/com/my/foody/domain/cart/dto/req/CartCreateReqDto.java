package com.my.foody.domain.cart.dto.req;

import com.my.foody.domain.cart.dto.resp.CartCreateRespDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CartCreateReqDto {
    private String storeName;
    private String menuName;
    private Long menuPrice;
    private Long quantity;
    private Long totalAmount;
    private Long minOrderAmount;
    private CartCreateRespDto cartCreateRespDto;
}
