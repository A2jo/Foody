package com.my.foody.domain.store.dto.req;

import java.time.LocalTime;

public class StoreCreateReqDto {
    private String name;
    private String description;
    private String contact;
    private Long minOrderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isDeleted;
}
