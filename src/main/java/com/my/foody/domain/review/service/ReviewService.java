package com.my.foody.domain.review.service;

import com.my.foody.domain.review.dto.resp.ReviewListRespDto;
import com.my.foody.domain.review.repo.ReviewRepository;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.config.valid.RequireAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public ReviewListRespDto getAllReviewByUser(Long userId) {
        User user = userService.findActivateUserByIdOrFail(userId);

        return null;
    }
}
