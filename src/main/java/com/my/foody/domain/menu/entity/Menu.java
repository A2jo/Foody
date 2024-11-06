package com.my.foody.domain.menu.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Boolean isSoldOut;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Builder
    public Menu(Long id, Store store, String name, Long price, Boolean isSoldOut, Boolean isDeleted) {
        this.id = id ;
        this.store = store;
        this.name = name;
        this.price = price;
        this.isSoldOut = false;
        this.isDeleted = false;
    }

    //메뉴명 수정
    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_MENU_NAME);
        }
        this.name = name;
    }
    
    //가격 수정
    public void updatePrice(Long price) {
        if (price == null || price < 1) {
            throw new BusinessException(ErrorCode.INVALID_MENU_PRICE);
        }
        this.price = price;
    }

    //삭제
    public void softDeleteMenu() {
        this.isDeleted = true;

    }
}
