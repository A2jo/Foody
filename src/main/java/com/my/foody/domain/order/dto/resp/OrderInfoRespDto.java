package com.my.foody.domain.order.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class OrderInfoRespDto {

    private LocalDateTime orderDate;
    private String orderStatus;
    private String storeName;
    private Long storeId;
    private Long orderId;
    private Long totalAmount;
    private String userContact;
    private String roadAddress;
    private String detailedAddress;
    private List<OrderInfoDto> orderList;

    @NoArgsConstructor
    @Getter
    public static class OrderInfoDto{
        private String menuName;
        private Long quantity;
        private Long amount;
    }

}
