package com.my.foody.domain.review.service;

import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.address.service.AddressService;
import com.my.foody.domain.review.dto.resp.ReviewListRespDto;
import com.my.foody.domain.review.entity.Review;
import com.my.foody.domain.review.repo.ReviewRepository;
import com.my.foody.domain.review.repo.dto.ReviewProjectionRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest extends DummyObject {
    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserService userService;


    @Test
    @DisplayName("유저 리뷰 목록 조회 테스트 성공")
    void getAllReviewByUser_Success() {
        Long userId = 1L;
        int page = 0;
        int limit = 10;
        Store store = mockStore();
        User user = newUser(userId);

        List<ReviewProjectionRespDto> reviewList =  new ArrayList<>();
        for(int i = 0;i<5;i++){
            Review review = newReview(store, user);
            reviewList.add(createMockReviewProjection(
                    (long)(i + 1),                // reviewId
                    store.getId(),                // storeId
                    store.getName(),              // storeName
                    review.getRating(),           // rating
                    review.getComment()           // comment
            ));
        }

        Page<ReviewProjectionRespDto> reviewPage;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());


        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(reviewRepository.findReviewsByUser(user, pageable)).thenReturn(reviewPage);

        // when
        ReviewListRespDto result = reviewService.getAllReviewByUser(userId, page, limit);

        // then
        assertNotNull(result);

        // 리뷰 목록 검증
        assertEquals(5, result.getReviewList().size());

        ReviewListRespDto.ReviewRespDto firstReview = result.getReviewList().get(0);
        assertEquals(1L, firstReview.getReviewId());
        assertEquals(store.getId(), firstReview.getStoreId());
        assertEquals(store.getName(), firstReview.getStoreName());
        assertEquals(result.getReviewList().get(0).getRating(), firstReview.getRating());
        assertEquals(result.getReviewList().get(0).getComment(), firstReview.getComment());

        // 페이징 정보 검증
        ReviewListRespDto.PageInfo pageInfo = result.getPageInfo();
        assertEquals(0, pageInfo.getPageNumber());
        assertEquals(10, pageInfo.getPageSize());
        assertEquals(5, pageInfo.getTotalElements());    // 5개의 리뷰
        assertEquals(1, pageInfo.getTotalPages());       // 10개 한 페이지라 1페이지
        assertTrue(pageInfo.isFirst());
        assertTrue(pageInfo.isLast());

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(reviewRepository).findReviewsByUser(user, pageable);
    }


    @Test
    @DisplayName("유저 리뷰 목록 조회 실패 테스트: 존재하지 않는 유저")
    void getAllReviewByUser_UserNotFound() {
        Long userId = 999L;
        int page = 0;
        int limit = 10;

        when(userService.findActivateUserByIdOrFail(userId))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        assertThrows(BusinessException.class,
                () -> reviewService.getAllReviewByUser(userId, page, limit));

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(reviewRepository, never()).findReviewsByUser(any(), any());
    }

    private ReviewProjectionRespDto createMockReviewProjection(
            Long reviewId, Long storeId, String storeName, Integer rating, String comment) {
        return new ReviewProjectionRespDto() {
            @Override
            public Long getReviewId() { return reviewId; }
            @Override
            public Long getStoreId() { return storeId; }
            @Override
            public String getStoreName() { return storeName; }
            @Override
            public Integer getRating() { return rating; }
            @Override
            public String getComment() { return comment; }
        };
    }

}