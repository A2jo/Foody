package com.my.foody.domain.review.repo.dto;

public interface ReviewProjectionRespDto {

    Long getReviewId();
    Long getStoreId();
    String getStoreName();
    String getComment();
    Integer getRating();
}
