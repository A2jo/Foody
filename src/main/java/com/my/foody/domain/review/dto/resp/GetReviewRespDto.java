package com.my.foody.domain.review.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewRespDto {
    private Long id;
    private String comments;
    private Integer rating;
}
