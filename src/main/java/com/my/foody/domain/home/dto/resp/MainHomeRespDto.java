package com.my.foody.domain.home.dto.resp;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MainHomeRespDto {
    private List<CategoryDto> categories;  // 카테고리 목록을 포함하는 필드

    @Getter
    @Setter
    public static class CategoryDto {
        private Long id;
        private String name;

        public CategoryDto(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
