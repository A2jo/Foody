package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class GetStoreRespDto {
    private String name;
    private String Description;
    private String contact;
    private Long MinOrderAmount;
    private LocalTime openTime;
    private LocalTime endTime;
    private boolean isDeleted;

    public GetStoreRespDto(Store store) {
        this.name = store.getName();
        this.Description = store.getDescription();
        this.contact = store.getContact();
        this.MinOrderAmount = store.getMinOrderAmount();
        this.openTime = store.getOpenTime();
        this.endTime = store.getEndTime();
        this.isDeleted = store.getIsDeleted();
    }
}
