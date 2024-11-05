package com.my.foody.domain.menu.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuCreateReqDto {

    @Size(min = 1, max = 30, message = "메뉴 이름은 1자 이상 30자 이하여야 합니다")
    @NotBlank(message = "메뉴 이름을 입력해야 합니다")
    private String name;

    @NotNull(message = "메뉴 가격을 입력해야 합니다")
    @Min(value = 1, message = "메뉴 가격은 1원 이상이어야 합니다")
    private Long price;

    public MenuCreateReqDto(String name, Long price) {
        this.name = name;
        this.price = price;
    }

}
