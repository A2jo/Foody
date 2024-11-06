package com.my.foody.domain.menu.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MenuUpdateRespDto {
    private static final String SUCCESS_MESSAGE = "메뉴 수정 되었습니다";
    private final String message;

    public MenuUpdateRespDto() {
        this.message = SUCCESS_MESSAGE;
    }

    public String getMessage() {
        return message;
    }

}
