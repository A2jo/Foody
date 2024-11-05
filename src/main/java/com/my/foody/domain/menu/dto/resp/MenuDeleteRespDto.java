package com.my.foody.domain.menu.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MenuDeleteRespDto {
    private static final String SUCCESS_MESSAGE = "메뉴 삭제 되었습니다";
    private final String message;

    public MenuDeleteRespDto() {
        this.message = SUCCESS_MESSAGE;
    }

    public String getMessage() {
        return message;
    }
}
