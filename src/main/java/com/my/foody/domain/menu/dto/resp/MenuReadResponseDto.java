package com.my.foody.domain.menu.dto.resp;

import com.my.foody.domain.menu.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuReadResponseDto {
    private String name;
    private Long price;

    public MenuReadResponseDto(Menu menu) {
        this.name = menu.getName();
        this.price = menu.getPrice();
    }
}
