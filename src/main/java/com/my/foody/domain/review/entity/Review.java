package com.my.foody.domain.review.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;
import jakarta.persistence.*;
import kotlin.BuilderInference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 255)
    private String comment;

    @Builder
    public Review(Long id, User user, Store store,Order order, Integer rating, String comment) {
        this.id = id;
        this.user = user;
        this.store = store;
        this.order = order;
        this.rating = rating;
        this.comment = comment;
    }
}
