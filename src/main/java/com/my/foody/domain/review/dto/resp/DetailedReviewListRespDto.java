package com.my.foody.domain.review.dto.resp;

import com.my.foody.domain.review.repo.dto.DetailedReviewProjectionRespDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class DetailedReviewListRespDto {

    public DetailedReviewListRespDto(Page<DetailedReviewProjectionRespDto> reviewRespDtoPage) {
        this.reviewList = reviewRespDtoPage.stream().map(DetailedReviewRespDto::new).collect(Collectors.toList());
        this.pageInfo = new PageInfo(reviewRespDtoPage);
    }

    private List<DetailedReviewRespDto> reviewList;
    private PageInfo pageInfo;

    @NoArgsConstructor
    @Getter
    public static class PageInfo {
        public PageInfo(Page<DetailedReviewProjectionRespDto> page) {
            this.pageNumber = page.getNumber();
            this.pageSize = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.isFirst = page.isFirst();
            this.isLast = page.isLast();
            this.hasNext = page.hasNext();
            this.hasPrevious = page.hasPrevious();
        }

        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean isFirst;
        private boolean isLast;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    @NoArgsConstructor
    @Getter
    public static class DetailedReviewRespDto {

        public DetailedReviewRespDto(DetailedReviewProjectionRespDto reviewRespDto) {
            this.reviewId = reviewRespDto.getReviewId();
            this.storeId = reviewRespDto.getStoreId();
            this.storeName = reviewRespDto.getStoreName();
            this.rating = reviewRespDto.getRating();
            this.comment = reviewRespDto.getComment();
            this.userNickname = reviewRespDto.getUserNickname();
        }

        private Long reviewId;
        private Long storeId;
        private String storeName;
        private Integer rating;
        private String comment;
        private String userNickname;
    }
}
