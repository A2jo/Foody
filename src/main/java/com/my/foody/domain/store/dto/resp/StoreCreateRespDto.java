package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;

import java.time.LocalTime;

public class StoreCreateRespDto {
    private String name;
    private String Description;
    private String contact;
    private Long MinOrderAmount;
    private LocalTime openTime;
    private LocalTime endTime;

    public StoreCreateRespDto(Store store) {
        this.name = store.getName();
        this.Description = store.getDescription();
        this.contact = store.getContact();
        this.MinOrderAmount = store.getMinOrderAmount();
        this.openTime = store.getOpenTime();
        this.endTime = store.getEndTime();
    }
}
