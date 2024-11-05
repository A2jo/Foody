package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class GetStoreRespDto {
    private String name;
    private boolean isDeleted;

    public GetStoreRespDto(Store store) {
        this.name = store.getName();
        this.isDeleted = store.getIsDeleted();
    }
}
