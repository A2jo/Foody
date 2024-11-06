package com.my.foody.domain.menu.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class MenuListRespDto {

    private List<GetMenuRespDto> menuList;
    private PageInfo pageInfo;

    public MenuListRespDto(Page<GetMenuRespDto> menuPage) {
        this.menuList = menuPage.getContent(); // 페이징된 가게 정보 리스트
        this.pageInfo = new MenuListRespDto.PageInfo(menuPage); // 페이징 정보
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        public PageInfo(Page<?> page) {
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
}
