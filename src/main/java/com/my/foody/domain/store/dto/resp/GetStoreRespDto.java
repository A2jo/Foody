package com.my.foody.domain.store.dto.resp;

import com.my.foody.domain.store.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetStoreRespDto {
    private Long storeId;
    private String name;
    private String description;
    private Long minOrderAmount;

    private Long minOrderAmount;

    private String description;
    private Long minOrderAmount;
    private boolean isDeleted;
    private String openTime;
    private String endTime;
    private int reviewCount;

    public GetStoreRespDto(Store store) {
        this.storeId = store.getId();
        this.name = store.getName();
        this.isDeleted = store.getIsDeleted();
        this.minOrderAmount = store.getMinOrderAmount();
        this.minOrderAmount = store.getMinOrderAmount();
        this.minOrderAmount = store.getMinOrderAmount();
    }

    public GetStoreRespDto(Long storeId, String name, Long minOrderAmount) {
        this.storeId = storeId;
        this.name = name;
        this.minOrderAmount = minOrderAmount;
    }

    public GetStoreRespDto(Long storeId, String name, String description, Long minOrderAmount, boolean isDeleted, String openTime, String endTime, int reviewCount) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.minOrderAmount = minOrderAmount;
        this.isDeleted = isDeleted;
        this.openTime = openTime;
        this.endTime = endTime;
        this.reviewCount = reviewCount;
    }

    public GetStoreRespDto(Long storeId, String name, Long minOrderAmount) {
        this.storeId = storeId;
        this.name = name;
        this.minOrderAmount = minOrderAmount;
    }

    public GetStoreRespDto(Long storeId, String name, String description, Long minOrderAmount, boolean isDeleted, String openTime, String endTime, int reviewCount) {
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
