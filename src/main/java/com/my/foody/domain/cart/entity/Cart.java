package com.my.foody.domain.cart.entity;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "cart")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    public void changeStore(Store store){
        this.store = store;
    }
}
