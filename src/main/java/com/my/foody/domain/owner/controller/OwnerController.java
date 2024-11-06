package com.my.foody.domain.owner.controller;


import com.my.foody.domain.owner.dto.req.OwnerDeleteReqDto;
import com.my.foody.domain.owner.dto.req.OwnerJoinReqDto;
import com.my.foody.domain.owner.dto.req.OwnerLoginReqDto;
import com.my.foody.domain.owner.dto.req.OwnerMyPageUpdateReqDto;
import com.my.foody.domain.owner.dto.resp.*;
import com.my.foody.domain.owner.service.OwnerService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResult<OwnerJoinRespDto>> signup(@RequestBody @Valid OwnerJoinReqDto ownerJoinReqDto) {
        OwnerJoinRespDto responseMessage = ownerService.signup(ownerJoinReqDto);
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<OwnerLoginRespDto>> login(@RequestBody @Valid OwnerLoginReqDto ownerLoginReqDto) {
        OwnerLoginRespDto responseMessage = ownerService.login(ownerLoginReqDto);
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResult<OwnerMyPageRespDto>> getMyPage(@CurrentUser TokenSubject tokenSubject) {
        Long ownerId = tokenSubject.getId();
        OwnerMyPageRespDto response = ownerService.getMyPage(ownerId);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    // 마이페이지 수정
    @PatchMapping("/mypage")
    public ResponseEntity<ApiResult<OwnerMyPageUpdateRespDto>> updateMyPage(
            @CurrentUser TokenSubject tokenSubject,
            @RequestBody @Valid OwnerMyPageUpdateReqDto updateReqDto) {

        Long ownerId = tokenSubject.getId();
        OwnerMyPageUpdateRespDto response = ownerService.updateMyPage(ownerId, updateReqDto);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    // 로그아웃 추가
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<OwnerLogoutRespDto>> logout() {
        OwnerLogoutRespDto responseMessage = ownerService.logout();
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

    // 회원 탈퇴 기능 추가
    @DeleteMapping
    public ResponseEntity<ApiResult<OwnerDeleteRespDto>> deleteOwner(
            @CurrentUser TokenSubject tokenSubject,
            @RequestBody @Valid OwnerDeleteReqDto deleteReqDto) {

        Long ownerId = tokenSubject.getId();
        OwnerDeleteRespDto responseMessage = ownerService.deleteOwner(ownerId, deleteReqDto);
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

}