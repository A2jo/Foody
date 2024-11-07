package com.my.foody.domain.review.service;

import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.order.repo.OrderRepository;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.review.dto.req.ReviewCreateReqDto;
import com.my.foody.domain.review.dto.resp.ReviewCreateRespDto;
import com.my.foody.domain.review.dto.resp.ReviewListRespDto;
import com.my.foody.domain.review.entity.Review;
import com.my.foody.domain.review.repo.ReviewRepository;
import com.my.foody.domain.review.repo.dto.ReviewProjectionRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final StoreService storeService;

    public ReviewListRespDto getAllReviewByUser(Long userId, int page, int limit) {
        User user = userService.findActivateUserByIdOrFail(userId);
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewProjectionRespDto> reviewPage = reviewRepository.findReviewsByUser(user, pageable);
        return new ReviewListRespDto(reviewPage);
    }

    @Transactional
    public ReviewCreateRespDto createReview(Long orderId, ReviewCreateReqDto requestDto, Long userId) {
        // 주문정보확인
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 해당 주문건에 리뷰가 이미 존재 하는지 확인
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 주문 상태 확인 (배달완료만 리뷰가능)
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        // 주문과 사용자 일치 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 가게 영업중인지 확인
            Store store = storeService.findActivateStoreByIdOrFail(order.getStore().getId());
        // 리뷰 생성
        Review review = Review.builder()
                .user(order.getUser())
                .store(store)
                .rating(requestDto.getRating())
                .order(order)
                .comment(requestDto.getComment())
                .build();

        reviewRepository.save(review);

        return new ReviewCreateRespDto();
    }
}
