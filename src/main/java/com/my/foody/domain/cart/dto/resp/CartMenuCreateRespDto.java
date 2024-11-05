package com.my.foody.domain.cart.dto.resp;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.CartMenuRepository;
import com.my.foody.domain.menu.entity.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CartMenuCreateRespDto {
    private Long cartId;
    private Long menuId;
    private Long quantity;

    public CartMenuCreateRespDto(CartMenu cartMenu) {
        this.cartId = cartMenu.getCart().getId();
        this.menuId = cartMenu.getMenu().getId();
        this.quantity = cartMenu.getQuantity();
    }
}
