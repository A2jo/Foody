package com.my.foody.domain.cart.dto.resp;

import com.my.foody.domain.cart.dto.req.CartCreateReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CartCreateRespDto {
    private String storeName;
    private String menuName;
    private Long menuPrice;
    private Long quantity;
    private Long totalAmount;
    private Long minOrderAmount;
    private CartCreateReqDto cartCreateReqDto;
}
