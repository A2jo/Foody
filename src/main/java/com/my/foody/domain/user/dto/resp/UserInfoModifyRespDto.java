package com.my.foody.domain.user.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserInfoModifyRespDto {

    private static final String SUCCESS_MESSAGE = "수정 완료 되었습니다";
    private String message;

    public UserInfoModifyRespDto(String message) {
        this.message = SUCCESS_MESSAGE;
    }
}
