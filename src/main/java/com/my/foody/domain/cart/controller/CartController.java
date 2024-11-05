package com.my.foody.domain.cart.controller;

import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.service.CartService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @RequireAuth(userType = UserType.USER) // User 인증 확인
    @GetMapping
    public ResponseEntity<ApiResult<Page<CartItemRespDto>>> getCartItems(
            @RequestParam int page,
            @RequestParam int limit,
            @CurrentUser TokenSubject tokenSubject) {

        Long userId = tokenSubject.getId();
        Page<CartItemRespDto> cartItems = cartService.getCartItems(userId, page, limit);

        return ResponseEntity.ok(ApiResult.success(cartItems));
    }

}
