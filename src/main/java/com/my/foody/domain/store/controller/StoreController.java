package com.my.foody.domain.store.controller;

import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.global.ex.CustomJwtException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.util.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final JwtProvider jwtProvider;

    @PostMapping("/stores")
    public ResponseEntity<ApiResult<StoreCreateRespDto>> createStore(@RequestBody @Valid StoreCreateReqDto storeCreatereqDto, HttpServletRequest request) {
        String token = extractToken(request);
        Long ownerId = jwtProvider.validate(token).getId();
        return new ResponseEntity<>(ApiResult.success(storeService.createStore(storeCreatereqDto, ownerId)), HttpStatus.CREATED);
    }

    // Token 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtVo.HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtVo.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtVo.TOKEN_PREFIX.length());
        }
        throw new CustomJwtException(ErrorCode.MISSING_BEARER_TOKEN);
    }
}
