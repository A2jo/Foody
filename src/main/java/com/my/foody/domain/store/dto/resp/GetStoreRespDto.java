package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class GetStoreRespDto {
    private Long storeId;
    private String name;
    private String description;
    private Long minOrderAmount;
    private Long reviewCount;
    private LocalTime openTime;
    private LocalTime endTime;
    private Boolean isDeleted;

    public GetStoreRespDto(Store store) {
        this.storeId = store.getId();
        this.name = store.getName();
        this.isDeleted = store.getIsDeleted();
        this.minOrderAmount = store.getMinOrderAmount();
    }

    public GetStoreRespDto(Store store, Long reviewCount) {
        this.name = store.getName();
        this.description = store.getDescription();
        this.reviewCount = reviewCount;
        this.minOrderAmount = store.getMinOrderAmount();
        this.openTime = store.getOpenTime();
        this.endTime = store.getEndTime();
    }

    public GetStoreRespDto(Long storeId, String name, Long minOrderAmount) {
        this.storeId = storeId;
        this.name = name;
        this.minOrderAmount = minOrderAmount;
    }

    public GetStoreRespDto(Long storeId, String name, String description, Long minOrderAmount, boolean isDeleted, LocalTime openTime, LocalTime endTime, Long reviewCount) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.minOrderAmount = minOrderAmount;
        this.isDeleted = isDeleted;
        this.openTime = openTime;
        this.endTime = endTime;
        this.reviewCount = reviewCount;
    }
}
