package com.my.foody.domain.menu.controller;

import com.my.foody.domain.menu.dto.req.MenuCreateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuCreateRespDto;
import com.my.foody.domain.menu.dto.resp.MenuReadResponseDto;
import com.my.foody.domain.menu.service.MenuService;
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

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 1. 메뉴등록
    @PostMapping("/api/owners/stores/{storeId}/menus")
    // 권한 확인
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<MenuCreateRespDto>> createMenu(@PathVariable("storeId") Long storeId,
                                                                   @Valid @RequestBody MenuCreateReqDto menuCreateReqDto,
                                                                   @CurrentUser TokenSubject tokenSubject) {
        //userId
        Long ownerId = tokenSubject.getId();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success(menuService.createMenu(storeId, menuCreateReqDto, ownerId)));
    }


    // 메뉴조회
    @GetMapping("/api/owners/stores/{storeId}/menus")
    //권한 확인
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<MenuReadResponseDto>> getMenus(@PathVariable("storeId") Long storeId,
                                                                         @CurrentUser TokenSubject tokenSubject,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int limit) {
        //ownerId
        Long ownerId = tokenSubject.getId();


        MenuReadResponseDto responseDto = menuService.getMenus(storeId, ownerId, page, limit);
        return ResponseEntity.ok(ApiResult.success(responseDto));
    }

}
