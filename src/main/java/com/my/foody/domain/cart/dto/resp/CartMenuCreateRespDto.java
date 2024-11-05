package com.my.foody.domain.cart.dto.resp;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.menu.entity.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CartMenuCreateRespDto {
    private Long cartId;

    private List<CartMenuRespDto> cartMenuList;

    @NoArgsConstructor
    @Getter
    public static class CartMenuRespDto{
        public CartMenuRespDto(CartMenu cartMenu) {
            this.menuId = cartMenu.getMenu().getId();
            this.quantity = cartMenu.getQuantity();
        }

        private Long menuId;
        private Long quantity;
    }
}
