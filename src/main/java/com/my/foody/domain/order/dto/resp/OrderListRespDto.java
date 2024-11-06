package com.my.foody.domain.order.dto.resp;

import com.my.foody.domain.orderMenu.repo.dto.OrderProjectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class OrderListRespDto {
    public OrderListRespDto(Page<OrderProjectionDto> orderList) {
        this.orderList = orderList.stream().map(OrderRespDto::new).toList();
        this.pageInfo = new PageInfo(orderList);
    }

    private List<OrderRespDto> orderList;
    private PageInfo pageInfo;

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    public static class PageInfo {
        public PageInfo(Page<OrderProjectionDto> page) {
            this.pageNumber = page.getNumber();
            this.pageSize = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.isFirst = page.isFirst();
            this.isLast = page.isLast();
            this.hasNext = page.hasNext();
            this.hasPrevious = page.hasPrevious();
        }

        private int pageNumber;        // 현재 페이지 번호
        private int pageSize;          // 페이지당 항목 수
        private long totalElements;    // 전체 항목 수
        private int totalPages;        // 전체 페이지 수
        private boolean isFirst;       // 첫 페이지 여부
        private boolean isLast;        // 마지막 페이지 여부
        private boolean hasNext;       // 다음 페이지 존재 여부
        private boolean hasPrevious;   // 이전 페이지 존재 여부
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    public static class OrderRespDto{

        public OrderRespDto(OrderProjectionDto dto) {
            this.storeName = dto.getStoreName();
            this.orderStatus = dto.getOrderStatus().getDescription();
            this.roadAddress = dto.getRoadAddress();
            this.detailedAddress = dto.getDetailedAddress();
            this.totalAmount = dto.getTotalAmount();
            this.menuSummary = createMenuSummary(dto.getMenuNames());
            this.orderId = dto.getOrderId();
            this.orderDate =  dto.getCreatedAt();
        }

        private String createMenuSummary(String menuNames) {
            if (menuNames == null || menuNames.isBlank()) {
                return "메뉴 정보 없음";
            }

            String[] menuArray = menuNames.split(",");
            return menuArray.length > 1
                    ? menuArray[0] + " 외 " + (menuArray.length - 1) + "개"
                    : menuArray[0];
        }

        private String storeName;
        private String orderStatus;
        private String roadAddress;
        private String detailedAddress;
        private Long totalAmount;
        private String menuSummary;
        private Long orderId;
        private LocalDateTime orderDate;
    }
}
