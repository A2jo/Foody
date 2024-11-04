package com.my.foody.domain.store.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class StoreCreateReqDto {
    private String name;
    private String description;
    private String contact;
    private Long minOrderAmount;
    private LocalTime openTime;
    private LocalTime endTime;
    private boolean isDeleted;
}
