package com.my.foody.domain.cart.dto.resp;

import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cartMenu.CartMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class CartItemListRespDto {
    private String storeName;
    private List<CartItemRespDto> cartItemList;
    private Long totalAmount;
    private Long minOrderAmount;

    private Long calculateTotalAmount(List<CartMenu> cartMenuList) {
        return cartMenuList.stream()
                .mapToLong(cartMenu -> cartMenu.getMenu().getPrice() * cartMenu.getQuantity())
                .sum();
    }

    @NoArgsConstructor
    @Getter
    public class CartItemRespDto{
        private String menuName;
        private Long menuPrice;
        private Long quantity;

        public CartItemRespDto(CartMenu cartMenu) {
            this.menuName = cartMenu.getMenu().getName();
            this.menuPrice = cartMenu.getMenu().getPrice();
            this.quantity = cartMenu.getQuantity();
        }
    }

    public CartItemListRespDto(Cart cart, List<CartMenu> cartMenuList) {
        this.storeName = cart.getStore().getName();
        this.cartItemList = cartMenuList.stream().map(CartItemRespDto::new).toList();
        this.totalAmount = calculateTotalAmount(cartMenuList);
        this.minOrderAmount = cart.getStore().getMinOrderAmount();
    }

    public Long getTotalOrderAmount() {
        return totalAmount;
    }
}
