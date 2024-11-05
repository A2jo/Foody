package com.my.foody.domain.review.dto.resp;

import com.my.foody.domain.review.repo.dto.ReviewProjectionRespDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ReviewListRespDto {

    public ReviewListRespDto(Page<ReviewProjectionRespDto> reviewRespDtoPage) {
        this.reviewList = reviewRespDtoPage.stream().map(ReviewRespDto::new).collect(Collectors.toList());
        this.pageInfo = new PageInfo(reviewRespDtoPage);
    }

    private List<ReviewRespDto> reviewList;
    private PageInfo pageInfo;

    @NoArgsConstructor
    @Getter
    public static class PageInfo {
        public PageInfo(Page<ReviewProjectionRespDto> page) {
            this.pageNumber = page.getNumber();
            this.pageSize = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.isFirst = page.isFirst();
            this.isLast = page.isLast();
            this.hasNext = page.hasNext();
            this.hasPrevious = page.hasPrevious();
        }

        private int pageNumber;        // 현재 페이지 번호
        private int pageSize;          // 페이지당 항목 수
        private long totalElements;    // 전체 항목 수
        private int totalPages;        // 전체 페이지 수
        private boolean isFirst;       // 첫 페이지 여부
        private boolean isLast;        // 마지막 페이지 여부
        private boolean hasNext;       // 다음 페이지 존재 여부
        private boolean hasPrevious;   // 이전 페이지 존재 여부
    }

    @NoArgsConstructor
    @Getter
    public static class ReviewRespDto{

        public ReviewRespDto(ReviewProjectionRespDto reviewRespDto) {
            this.reviewId = reviewRespDto.getReviewId();
            this.storeId = reviewRespDto.getStoreId();
            this.storeName = reviewRespDto.getStoreName();
            this.rating = reviewRespDto.getRating();
            this.comment = reviewRespDto.getComment();
        }

        private Long reviewId;
        private Long storeId;
        private String storeName;
        private Integer rating;
        private String comment;
    }
}
