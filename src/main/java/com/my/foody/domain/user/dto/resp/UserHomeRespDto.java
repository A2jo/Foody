package com.my.foody.domain.user.dto.resp;

import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.store.entity.Store;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserHomeRespDto {
    private final List<CategoryDto> categories;
    private final List<StoreDto> stores;

    public UserHomeRespDto(List<Category> categories, List<Store> stores) {
        this.categories = categories.stream().map(CategoryDto::fromEntity).collect(Collectors.toList());
        this.stores = stores.stream().map(StoreDto::fromEntity).collect(Collectors.toList());
    }

    // 카테고리와 가게 정보를 위한 내부 DTO 클래스
    @Getter
    public static class CategoryDto {
        private Long id;
        private String name;

        public static CategoryDto fromEntity(Category category) {
            CategoryDto dto = new CategoryDto();
            dto.id = category.getId();
            dto.name = category.getName();
            return dto;
        }
    }

    @Getter
    public static class StoreDto {
        private Long id;
        private String name;
        private Long minOrderAmount;

        public static StoreDto fromEntity(Store store) {
            StoreDto dto = new StoreDto();
            dto.id = store.getId();
            dto.name = store.getName();
            dto.minOrderAmount = store.getMinOrderAmount();
            return dto;
        }
    }
}