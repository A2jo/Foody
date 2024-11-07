package com.my.foody.domain.review.repo.dto;

public interface DetailedReviewProjectionRespDto {
    Long getReviewId();
    Long getStoreId();
    String getStoreName();
    String getComment();
    Integer getRating();
    String getUserNickname(); // 사용자 닉네임 추가
}
