package com.my.foody.domain.menu.controller;


import com.my.foody.domain.menu.dto.resp.MenuReadResponseDto;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 메뉴조회
    @GetMapping("/api/owners/stores/{storeId}/menus")
    //권한 확인
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<Page<MenuReadResponseDto>>> getMenus(@PathVariable("storeId") Long storeId,
                                                                         @CurrentUser TokenSubject tokenSubject,
                                                                         Pageable pageable) {
        //ownerId
        Long ownerId = tokenSubject.getId();

        Page<MenuReadResponseDto> responseDto = menuService.getMenus(storeId, ownerId, pageable);
        return ResponseEntity.ok(ApiResult.success(responseDto));
    }
}
