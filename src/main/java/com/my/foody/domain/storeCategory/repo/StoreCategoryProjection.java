package com.my.foody.domain.storeCategory.repo;

public interface StoreCategoryProjection {
    Long getStoreId();
    String getStoreName();
    Long getMinOrderAmount();
}
