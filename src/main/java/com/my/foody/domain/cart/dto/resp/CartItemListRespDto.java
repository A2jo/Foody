package com.my.foody.domain.cart.dto.resp;

import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cartMenu.CartMenu;
import com.my.foody.domain.cartMenu.dto.CartMenuDetailProjectionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class CartItemListRespDto {

    public CartItemListRespDto(Page<CartMenuDetailProjectionDto> dto, Cart cart) {
        this.storeName = cart.getStore().getName();
        this.totalAmount = calculateTotalAmount(dto);
        this.minOrderAmount = cart.getStore().getMinOrderAmount();
        this.cartMenuList = dto.stream().map(CartMenuDto::new).toList();
    }
    private Long calculateTotalAmount(Page<CartMenuDetailProjectionDto> projectionDto) {
        return projectionDto.getContent().stream()
                .mapToLong(dto -> dto.getPrice() * dto.getQuantity())
                .sum();
    }

    private String storeName;
    private Long totalAmount;
    private Long minOrderAmount;
    private List<CartMenuDto> cartMenuList;

    @NoArgsConstructor
    @Getter
    public static class CartMenuDto{
        public CartMenuDto(CartMenuDetailProjectionDto dto) {
            this.menuName = dto.getMenuName();
            this.menuPrice = dto.getPrice();
            this.quantity = dto.getQuantity();
        }

        private String menuName;
        private Long menuPrice;
        private Long quantity;
    }
}
