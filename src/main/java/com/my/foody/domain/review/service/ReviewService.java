package com.my.foody.domain.review.service;

import com.my.foody.domain.review.dto.resp.ReviewListRespDto;
import com.my.foody.domain.review.repo.ReviewRepository;
import com.my.foody.domain.review.repo.dto.ReviewProjectionRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
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

    public ReviewListRespDto getAllReviewByUser(Long userId, int page, int limit) {
        User user = userService.findActivateUserByIdOrFail(userId);
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewProjectionRespDto> reviewPage = reviewRepository.findReviewsByUser(user, pageable);
        return new ReviewListRespDto(reviewPage);
    }
}
