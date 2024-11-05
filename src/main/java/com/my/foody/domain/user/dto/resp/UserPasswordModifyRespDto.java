package com.my.foody.domain.user.dto.resp;

import lombok.Getter;

@Getter
public class UserPasswordModifyRespDto {

    private static final String SUCCESS_MESSAGE = "변경 되었습니다";
    private String message;

    public UserPasswordModifyRespDto() {
        this.message = SUCCESS_MESSAGE;
    }
}
