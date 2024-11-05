package com.my.foody.domain.menu.controller;

import com.my.foody.domain.menu.dto.resp.MenuDeleteRespDto;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 메뉴삭제
    @PatchMapping("/api/owners/stores/{storeId}/menus/{menuId}")
    //권한 확인
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<MenuDeleteRespDto>> softDeleteMenu(@PathVariable("storeId") Long storeId, @PathVariable("menuId") Long menuId,
                                                                       @CurrentUser TokenSubject tokenSubject) {
        //ownerId
        Long ownerId = tokenSubject.getId();
        MenuDeleteRespDto responseDto = menuService.SoftDeleteMenu(storeId, menuId, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.success(responseDto));
    }
}
