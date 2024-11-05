package com.my.foody.domain.cartMenu;

import com.my.foody.domain.base.BaseEntity;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private Long quantity;

    @Builder
    public CartMenu(Long id, Cart cart, Menu menu, Long quantity) {
        this.id = id;
        this.cart = cart;
        this.menu = menu;
        this.quantity = quantity;
    }
}
