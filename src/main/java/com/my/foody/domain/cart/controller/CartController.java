package com.my.foody.domain.cart.controller;

import com.my.foody.domain.cart.dto.req.CartMenuCreateReqDto;
import com.my.foody.domain.cart.dto.resp.CartItemListRespDto;
import com.my.foody.domain.cart.dto.resp.CartItemRespDto;
import com.my.foody.domain.cart.dto.resp.CartMenuCreateRespDto;
import com.my.foody.domain.cart.service.CartService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @RequireAuth(userType = UserType.USER)
    @GetMapping("/cart")
    public ResponseEntity<ApiResult<CartItemListRespDto>> getCartItems(@RequestParam(value = "page", required = false, defaultValue = "0") @Min(value = 0) int page,
                                                                        @RequestParam(value = "limit", required = false, defaultValue = "10") @Positive int limit,
                                                                        @CurrentUser TokenSubject tokenSubject) {
        return new ResponseEntity<>(ApiResult.success(cartService.getCartItems(tokenSubject.getId(), page, limit)), HttpStatus.OK);
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
