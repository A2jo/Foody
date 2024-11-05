package com.my.foody.domain.cart.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CartItemRespDto {
    private String storeName;
    private String menuName;
    private Long menuPrice;
    private Long quantity;
    private Long totalAmount;
    private Long minOrderAmount;

    public Long getTotalOrderAmount() {
        return totalAmount;
    }
}
