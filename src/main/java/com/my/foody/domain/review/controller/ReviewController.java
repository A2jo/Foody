package com.my.foody.domain.review.controller;

import com.my.foody.domain.review.dto.req.ReviewCreateReqDto;
import com.my.foody.domain.review.dto.resp.ReviewCreateRespDto;
import com.my.foody.domain.review.service.ReviewService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/orders/{orderId}/reviews")
    @RequireAuth(userType = UserType.USER)
    public ResponseEntity<ApiResult<ReviewCreateRespDto>> createReview(@PathVariable("orderId") Long orderId, @Valid @RequestBody ReviewCreateReqDto reviewCreateReqDto,
                                                                       @CurrentUser TokenSubject tokenSubject) {

        // userId
        Long userId = tokenSubject.getId();

        ReviewCreateRespDto responseDto = reviewService.createReview(orderId, reviewCreateReqDto, userId);

        return ResponseEntity.ok(ApiResult.success(responseDto));
    }

}
