package com.my.foody.domain.store.controller;

import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.GetStoreRespDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
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

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @RequireAuth(userType = UserType.OWNER)
    @PostMapping("/stores")
    public ResponseEntity<ApiResult<StoreCreateRespDto>> createStore(@RequestBody @Valid StoreCreateReqDto storeCreatereqDto,@CurrentUser TokenSubject tokenSubject) {
        Long ownerId = tokenSubject.getId();
        return new ResponseEntity<>(ApiResult.success(storeService.createStore(storeCreatereqDto, ownerId)), HttpStatus.CREATED);
    }

//    @RequireAuth(userType = UserType.OWNER)
//    @GetMapping("stores")
//    public ResponseEntity<ApiResult<GetStoreRespDto>> getAllStoreByOwnerId(TokenSubject tokenSubject) {
//        Long ownerId = tokenSubject.getId();
//        return new ResponseEntity<>(ApiResult.success(storeService.getAllStoreByOwnerId(ownerId)), HttpStatus.CREATED);
//    }
}
