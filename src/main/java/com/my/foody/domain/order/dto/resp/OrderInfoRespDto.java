package com.my.foody.domain.order.dto.resp;

import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.orderMenu.repo.dto.OrderMenuProjectionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class OrderInfoRespDto {

    public OrderInfoRespDto(List<OrderMenuProjectionDto> dto, Order order) {
        this.orderDate = order.getCreatedAt();
        this.orderStatus = order.getOrderStatus().getDescription();
        this.storeName = order.getStore().getName();
        this.storeId = order.getStore().getId();
        this.orderId = order.getId();
        this.totalAmount = order.getTotalAmount();
        this.userContact = order.getUser().getContact();
        this.roadAddress = order.getAddress().getRoadAddress();
        this.detailedAddress = order.getAddress().getDetailedAddress();
        this.orderList = dto.stream().map(OrderInfoDto::new).toList();
    }

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
        public OrderInfoDto(OrderMenuProjectionDto dto) {
            this.menuName = dto.getMenuName();
            this.quantity = dto.getQuantity();
            this.amount = dto.getPrice();
            this.menuId = dto.getMenuId();
        }

        private String menuName;
        private Long quantity;
        private Long amount;
        private Long menuId;
    }

}
