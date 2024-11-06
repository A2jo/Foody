package com.my.foody.domain.search.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class SearchReqDto {
    @NotBlank(message = "키워드는 공백일 수 없습니다.")
    private String keyword;
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page;
    @Positive(message = "최소 1개의 결과를 요청해야 합니다.")
    private int limit;

    public SearchReqDto(String keyword, int page, int limit) {
        this.keyword = keyword;
        this.page = page;
        this.limit = limit;
    }
}
