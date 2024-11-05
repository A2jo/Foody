package com.my.foody.domain.order.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderPreviewRespDto {
    private String roadAddress;
    private String detailedAddress;
    private String userContact;
    private String storeName;
    private Long storeId;
    private String menuName;
    private Long menuPrice;
    private Long quantity;
    private Long totalAmount;
}
