package com.my.foody.domain.store.controller;

import com.my.foody.domain.menu.dto.resp.MenuListRespDto;
import com.my.foody.domain.store.dto.req.ModifyStoreReqDto;
import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.GetStoreRespDto;
import com.my.foody.domain.store.dto.resp.ModifyStoreRespDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.dto.resp.StoreListRespDto;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @RequireAuth(userType = UserType.OWNER)
    @PostMapping("/owners/stores")
    public ResponseEntity<ApiResult<StoreCreateRespDto>> createStore(@RequestBody @Valid StoreCreateReqDto storeCreatereqDto,
                                                                     @CurrentUser TokenSubject tokenSubject) {
        Long ownerId = tokenSubject.getId();
        return new ResponseEntity<>(ApiResult.success(storeService.createStore(storeCreatereqDto, ownerId)), HttpStatus.CREATED);
    }

    @RequireAuth(userType = UserType.OWNER)
    @GetMapping("/owners/stores")
    public ResponseEntity<ApiResult<List<GetStoreRespDto>>> getAllStoresByOwnerId(@CurrentUser TokenSubject tokenSubject) {
        Long ownerId = tokenSubject.getId();
        List<GetStoreRespDto> stores = storeService.getAllStoresByOwnerId(ownerId);
        return ResponseEntity.ok(ApiResult.success(stores));
    }

    @RequireAuth(userType = UserType.OWNER)
    @PatchMapping("/stores/{storeId}")
    public ResponseEntity<ApiResult<ModifyStoreRespDto>> modifyStore(@PathVariable(value = "storeId") Long storeId,
                                                                     @RequestBody @Valid ModifyStoreReqDto modifyStoreReqDto,
                                                                     @CurrentUser TokenSubject tokenSubject) {
        Long ownerId = tokenSubject.getId();
        return new ResponseEntity<>(ApiResult.success(storeService.modifyStore(storeId, modifyStoreReqDto, ownerId)), HttpStatus.OK);
    }

    @GetMapping("/home/categories/{categoryId}/store")
    public ResponseEntity<ApiResult<StoreListRespDto>> getStoreByCategory(@PathVariable(value = "categoryId") Long categoryId,
                                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                                          @RequestParam(value = "limit", defaultValue = "10") int limit) {
        StoreListRespDto storeListRespDto = storeService.getStoreByCategory(categoryId, page, limit);
        return ResponseEntity.ok(ApiResult.success(storeListRespDto));
    }

    @GetMapping("/home/categories/{categoryId}/stores/{storeId}/menus")
    public ResponseEntity<ApiResult<MenuListRespDto>> getStoreMenus (@PathVariable(value = "storeId") Long storeId,
                                                                     @PathVariable(value = "categoryId") Long categoryId,
                                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "limit", defaultValue = "10") int limit) {
        MenuListRespDto menuListRespDto = storeService.getStoreMenus(storeId, categoryId, page, limit);
        return ResponseEntity.ok(ApiResult.success(menuListRespDto));
    }
}
