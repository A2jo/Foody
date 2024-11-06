package com.my.foody.domain.orderMenu.entity;

import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "order_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long menuId; //Menu와의 관계를 빼고 참고용으로 menuId만 기재

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long price;
}
