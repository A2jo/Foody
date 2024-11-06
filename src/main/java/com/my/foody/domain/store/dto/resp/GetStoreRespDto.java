package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class GetStoreRespDto {
    private String name;
    private Long minOrderAmount;
    private Long reviewCount;
    private String description;
    private LocalTime openTime;
    private LocalTime endTime;

    private boolean isDeleted;

    public GetStoreRespDto(Store store) {
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
        this.name = name;
        this.minOrderAmount = minOrderAmount;
    }
}
