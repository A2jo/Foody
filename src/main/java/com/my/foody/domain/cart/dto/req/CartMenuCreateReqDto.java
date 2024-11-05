package com.my.foody.domain.cart.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CartMenuCreateReqDto {

    @NotNull(message = "수량을 입력해주세요")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    private Long quantity;
}
