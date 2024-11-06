package com.my.foody.domain.order.dto.resp;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPreviewRespDto {
    public OrderPreviewRespDto(User user, Store store, Cart cart, List<CartMenu> cartMenuList, Address address, Long totalAmount) {
        this.roadAddress = address.getRoadAddress();
        this.detailedAddress = address.getDetailedAddress();
        this.userContact = user.getContact();
        this.storeName = store.getName();
        this.storeId = store.getId();
        this.totalAmount = totalAmount;
        this.cartId = cart.getId();
        this.cartMenuList = cartMenuList.stream()
                .map(CartMenuRespDto::new)
                .collect(Collectors.toList());
    }

    private String roadAddress;
    private String detailedAddress;
    private String userContact;
    private String storeName;
    private Long storeId;
    private List<CartMenuRespDto> cartMenuList;
    private Long totalAmount;
    private Long cartId;

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class CartMenuRespDto{

        public CartMenuRespDto(CartMenu cartMenu) {
            this.menuName = cartMenu.getMenu().getName();
            this.menuPrice = cartMenu.getMenu().getPrice();
            this.quantity = cartMenu.getQuantity();
        }

        private String menuName;
        private Long menuPrice;
        private Long quantity;

    }
}
