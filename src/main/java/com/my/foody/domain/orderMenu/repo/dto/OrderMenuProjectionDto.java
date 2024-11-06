package com.my.foody.domain.orderMenu.repo.dto;

public interface OrderMenuProjectionDto {

    Long getQuantity();
    Long getPrice();
    Long getMenuId();
    String getMenuName();
}
