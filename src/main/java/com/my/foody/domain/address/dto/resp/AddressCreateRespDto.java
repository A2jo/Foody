package com.my.foody.domain.address.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class AddressCreateRespDto {
    private static final String SUCCESS_MESSAGE = "등록 완료 되었습니다";
    private final String message;

    public AddressCreateRespDto() {
        this.message = SUCCESS_MESSAGE;
    }

}
