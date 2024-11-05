package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;
import lombok.Getter;

@Getter

public class StoreCreateRespDto {
    private final String SUCCESS_MESSAGE = "가게가 생성되었습니다.";
    private String message;

    public StoreCreateRespDto(Store store) {
        this.message = SUCCESS_MESSAGE;
    }
}
