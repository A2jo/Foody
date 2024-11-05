package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchStoreRespDto {

    private final String SUCCESS_MESSAGE = "가게 정보가 수정되었습니다..";
    private String message;

    public PatchStoreRespDto(Store store) {
        this.message = SUCCESS_MESSAGE;
    }
}
