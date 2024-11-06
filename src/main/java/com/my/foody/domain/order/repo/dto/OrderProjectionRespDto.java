package com.my.foody.domain.order.repo.dto;

import com.my.foody.domain.owner.entity.OrderStatus;

import java.time.LocalDateTime;

public interface OrderProjectionRespDto {

    String getStoreName();
    OrderStatus getOrderStatus();
    String getRoadAddress();
    String getDetailedAddress();
    String getMenuNames();
    Long getOrderId();

    Long getTotalAmount();

    LocalDateTime getCreatedAt();
}
