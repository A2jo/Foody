package com.my.foody.domain.owner.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("대기중"),
    ACCEPTED("접수완료"),
    CANCELED("취소됨"),
    COMPLETED("배달완료");

    private final String description;
}
