package com.my.foody.domain.menu.repo;

public interface MenuProjection {
    Long getId();
    String getName();
    Long getPrice();
    Boolean getIsDeleted();
    Boolean getIsSoldOut();
}
