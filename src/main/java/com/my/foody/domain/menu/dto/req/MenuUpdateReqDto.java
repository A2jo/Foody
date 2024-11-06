package com.my.foody.domain.menu.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuUpdateReqDto {
    private String name;
    private Long price;

    public MenuUpdateReqDto(String name, Long price) {
        this.name = name;
        this.price = price;
    }
}
