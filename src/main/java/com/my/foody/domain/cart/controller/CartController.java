package com.my.foody.domain.cart.controller;

import com.my.foody.domain.cart.dto.req.CartMenuCreateReqDto;
import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.dto.resp.CartMenuCreateRespDto;
import com.my.foody.domain.cart.service.CartService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    //TODO  이거 수정해야함!!!!
    @RequireAuth(userType = UserType.USER) // User 인증 확인
    @GetMapping("/cart")
    public ResponseEntity<ApiResult<Page<CartItemRespDto>>> getCartItems(
            @RequestParam int page,
            @RequestParam int limit,
            @CurrentUser TokenSubject tokenSubject) {

        Long userId = tokenSubject.getId();
        Page<CartItemRespDto> cartItems = cartService.getCartItems(userId, page, limit);

        return ResponseEntity.ok(ApiResult.success(cartItems));
    }

    @RequireAuth(userType = UserType.USER)
    @PostMapping("/home/stores/{storeId}/menus/{menuId}")
    public ResponseEntity<ApiResult<CartMenuCreateRespDto>> addCartItem(@PathVariable(value = "storeId")Long storeId,
                                                                        @PathVariable(value = "menuId")Long menuId,
                                                                        @RequestBody @Valid CartMenuCreateReqDto cartMenuCreateReqDto,
                                                                        @CurrentUser TokenSubject tokenSubject){
        return new ResponseEntity<>(ApiResult.success(cartService.addCartItem(storeId, menuId, cartMenuCreateReqDto, tokenSubject.getId())), HttpStatus.CREATED);
    }
}
