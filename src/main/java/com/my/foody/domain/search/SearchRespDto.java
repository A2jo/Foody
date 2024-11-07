package com.my.foody.domain.search;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchRespDto {
    private List<StoreDto> stores;

    public SearchRespDto(List<StoreDto> stores) {
        this.stores = stores;
    }

    @Getter
    @Setter
    public static class StoreDto {
        private Long id;
        private String name;
        private long minOrderAmount;

        public StoreDto(Long id, String name, long minOrderAmount) {
            this.id = id;
            this.name = name;
            this.minOrderAmount = minOrderAmount;
        }
    }
}
