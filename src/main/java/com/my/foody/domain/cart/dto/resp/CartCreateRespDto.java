package com.my.foody.domain.cart.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CartCreateRespDto {
    private int page;
    private int limit;
    private long totalItems;
    private int totalPages;
}
