package com.my.foody.domain.review.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ReviewListRespDto {

    private List<ReviewRespDto> reviewList;

    @NoArgsConstructor
    @Getter
    public static class ReviewRespDto{
        private Long reviewId;
        private Long storeId;
        private String storeName;
        private Integer rating;
        private String comment;
    }
}
