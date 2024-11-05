package com.my.foody.domain.user.dto.resp;

import lombok.Getter;


@Getter
public class AddressDeleteRespDto {

    private static final String SUCCESS_MESSAGE = "삭제 완료 되었습니다";
    private final String message;

    public AddressDeleteRespDto() {
        this.message = SUCCESS_MESSAGE;
    }
}
