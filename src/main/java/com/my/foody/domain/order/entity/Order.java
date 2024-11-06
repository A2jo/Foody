package com.my.foody.domain.order.entity;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING; //기본값 설정

    @Builder
    public Order(Long id, User user, Store store, Address address, Long totalAmount, OrderStatus orderStatus) {
        this.id = id;
    public Order(OrderStatus orderStatus, User user, Store store, Address address, Long totalAmount, Long id) {
        this.orderStatus = orderStatus;
    public Order(Long id, User user, Store store, Address address, Long totalAmount, OrderStatus orderStatus) {
        this.user = user;
        this.store = store;
        this.address = address;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.id = id;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void isStoreOwner(Long ownerId) {
        if (!this.store.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
