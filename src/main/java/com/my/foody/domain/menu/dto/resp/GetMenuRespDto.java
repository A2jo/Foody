package com.my.foody.domain.menu.dto.resp;

import com.my.foody.domain.menu.entity.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetMenuRespDto {
    private Long id;
    private String name;
    private Long price;

    public GetMenuRespDto(Long id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
