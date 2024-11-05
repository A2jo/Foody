package com.my.foody.domain.owner.controller;


import com.my.foody.domain.owner.dto.req.OwnerJoinReqDto;
import com.my.foody.domain.owner.dto.req.OwnerLoginReqDto;
import com.my.foody.domain.owner.dto.req.OwnerMyPageUpdateReqDto;
import com.my.foody.domain.owner.dto.resp.OwnerJoinRespDto;
import com.my.foody.domain.owner.dto.resp.OwnerLoginRespDto;
import com.my.foody.domain.owner.dto.resp.OwnerMyPageRespDto;
import com.my.foody.domain.owner.dto.resp.OwnerMyPageUpdateRespDto;
import com.my.foody.domain.owner.service.OwnerService;
import com.my.foody.global.util.api.ApiResult;
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
    public ResponseEntity<ApiResult<OwnerJoinRespDto>> signup(@RequestBody OwnerJoinReqDto ownerJoinReqDto) {
        OwnerJoinRespDto responseMessage = ownerService.signup(ownerJoinReqDto);
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<OwnerLoginRespDto>> login(@RequestBody OwnerLoginReqDto ownerLoginReqDto) {
        OwnerLoginRespDto responseMessage = ownerService.login(ownerLoginReqDto);
        return ResponseEntity.ok(ApiResult.success(responseMessage));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResult<OwnerMyPageRespDto>> getMyPage(@AuthenticationPrincipal Long ownerId) {
        OwnerMyPageRespDto response = ownerService.getMyPage(ownerId);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    // 마이페이지 수정
    @PatchMapping("/mypage")
    public ResponseEntity<ApiResult<OwnerMyPageUpdateRespDto>> updateMyPage(
            @AuthenticationPrincipal Long ownerId,
            @RequestBody OwnerMyPageUpdateReqDto updateReqDto) {

        // 서비스에서 업데이트 처리 후 응답 DTO 반환
        OwnerMyPageUpdateRespDto response = ownerService.updateMyPage(ownerId, updateReqDto);
        return ResponseEntity.ok(ApiResult.success(response));
    }
}