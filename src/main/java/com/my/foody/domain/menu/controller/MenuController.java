package com.my.foody.domain.menu.controller;

import com.my.foody.domain.menu.dto.req.MenuUpdateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuUpdateRespDto;
import com.my.foody.domain.menu.service.MenuService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 2. 메뉴수정
    @PutMapping("/api/owners/stores/{storeId}/menus/{menuId}")
    //권한 확인
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<MenuUpdateRespDto>> updateMenu(@PathVariable("storeId") Long storeId, @PathVariable("menuId") Long menuId,
                                                                   @Valid @RequestBody MenuUpdateReqDto menuUpdateReqDto,
                                                                   @CurrentUser TokenSubject tokenSubject) {
        //userId
        Long ownerId = tokenSubject.getId();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResult.success(menuService.updateMenu(storeId, menuId, menuUpdateReqDto, ownerId)));

    }
}
