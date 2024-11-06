package com.my.foody.domain.user.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UserLogoutRespDto {

    private static final String SUCCESS_MESSAGE = "로그아웃 되었습니다";
    private final String message;

    public UserLogoutRespDto() {
        this.message = SUCCESS_MESSAGE;
    }
}
