package com.my.foody.domain.user.dto.resp;

import lombok.Getter;

@Getter
public class UserSignUpRespDto {
    private static final String SUCCESS_MESSAGE = "회원가입 되었습니다";
    private final String message;

    public UserSignUpRespDto() {
        this.message = SUCCESS_MESSAGE;
    }
}
