package com.my.foody.domain.review.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewCreateReqDto {
    @NotBlank(message = "리뷰 내용을 입력해 주세요")
    private String comment;

    @NotNull(message = "별점을 입력해 주세요")
    @Min(value = 1, message = "별점은 1 이상이어야 합니다")
    @Max(value = 5, message = "별점은 5 이하여야 합니다")
    private Integer rating;

    public ReviewCreateReqDto(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
