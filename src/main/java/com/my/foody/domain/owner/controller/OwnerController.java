package com.my.foody.domain.owner.controller;

import com.my.foody.domain.owner.dto.resp.OwnerJoinRespDto;
import com.my.foody.domain.owner.service.OwnerService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;
    private final JwtProvider jwtProvider;

    @PostMapping("/signup")
    public ResponseEntity<ApiResult<OwnerJoinRespDto>> signup(@RequestBody @Valid OwnerJoinReqDto ownerJoinReqDto) {
        OwnerJoinRespDto responseMessage = ownerService.signup(ownerJoinReqDto);
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<OwnerLoginRespDto>> login(@RequestBody @Valid OwnerLoginReqDto ownerLoginReqDto) {
        OwnerLoginRespDto responseMessage = ownerService.login(ownerLoginReqDto);

        // 응답 헤더에 토큰 추가
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + responseMessage.getToken())
                .body(ApiResult.success(responseMessage));
    }

    @GetMapping("/mypage")
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<OwnerMyPageRespDto>> getMyPage(@CurrentUser TokenSubject tokenSubject) {
        Long ownerId = tokenSubject.getId();
        OwnerMyPageRespDto response = ownerService.getMyPage(ownerId);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    // 마이페이지 수정
    @PatchMapping("/mypage")
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<OwnerMyPageUpdateRespDto>> updateMyPage(
            @CurrentUser TokenSubject tokenSubject,
            @RequestBody @Valid OwnerMyPageUpdateReqDto updateReqDto) {

        Long ownerId = tokenSubject.getId();
        OwnerMyPageUpdateRespDto response = ownerService.updateMyPage(ownerId, updateReqDto);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    // 로그아웃 추가
//    @PostMapping("/logout")
//    @RequireAuth(userType = UserType.OWNER)
//    public ResponseEntity<ApiResult<OwnerLogoutRespDto>> logout(@CurrentUser TokenSubject tokenSubject, @RequestHeader("Authorization") String token) {
//        ownerService.logout(tokenSubject, token);
//        return ResponseEntity.ok()
//                .header("Authorization", "") // 클라이언트에서 토큰을 삭제하도록 헤더를 비움
//                .body(ApiResult.success(responseMessage));
//    }

    // 회원 탈퇴 기능 추가
    @DeleteMapping
    @RequireAuth(userType = UserType.OWNER)
    public ResponseEntity<ApiResult<OwnerDeleteRespDto>> deleteOwner(
            @CurrentUser TokenSubject tokenSubject,
            @RequestBody @Valid OwnerDeleteReqDto deleteReqDto) {

        Long ownerId = tokenSubject.getId();
        OwnerDeleteRespDto responseMessage = ownerService.deleteOwner(ownerId, deleteReqDto);
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

}
