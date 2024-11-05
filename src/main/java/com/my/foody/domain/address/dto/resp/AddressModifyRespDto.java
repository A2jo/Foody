package com.my.foody.domain.address.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
public class AddressModifyRespDto {
    private static final String SUCCESS_MESSAGE = "수정 완료 되었습니다";
    private final String message;

    public AddressModifyRespDto() {
        this.message = SUCCESS_MESSAGE;
    }
}
