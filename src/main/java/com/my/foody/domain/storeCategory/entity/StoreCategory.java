package com.my.foody.domain.storeCategory.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "store_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public StoreCategory(Store store, Category category) {
        this.store = store;
        this.category = category;
    }
}
