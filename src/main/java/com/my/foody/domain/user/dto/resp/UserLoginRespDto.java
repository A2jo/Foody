package com.my.foody.domain.user.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserLoginRespDto{
    private static final String SUCCESS_MESSAGE = "로그인 되었습니다";
    private String message;
    @JsonIgnore
    private String token;

    public UserLoginRespDto(String token) {
        this.token = token;
        this.message = SUCCESS_MESSAGE;
    }
}