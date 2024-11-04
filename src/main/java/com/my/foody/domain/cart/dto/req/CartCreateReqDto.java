package com.my.foody.domain.cart.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CartCreateReqDto {
    private int page; // current page number
    private int limit; // page size
    private long totalItems; // total number of items in the cart
    private int totalPages;
}
