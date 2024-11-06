package com.my.foody.domain.review.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReviewCreateRespDto {
    private static final String SUCCESS_MESSAGE = "등록 완료 되었습니다";
    private final String message;

    public ReviewCreateRespDto() {
        this.message = SUCCESS_MESSAGE;
    }

    public String getMessage() {
        return message;
    }
}
