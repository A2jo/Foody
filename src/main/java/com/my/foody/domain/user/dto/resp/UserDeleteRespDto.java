package com.my.foody.domain.user.dto.resp;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UserDeleteRespDto {

    private static final String SUCCESS_MESSAGE = "삭제 완료 되었습니다";
    private final String message;

    public UserDeleteRespDto() {
        this.message = SUCCESS_MESSAGE;
    }
}
