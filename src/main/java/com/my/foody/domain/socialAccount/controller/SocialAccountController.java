package com.my.foody.domain.socialAccount.controller;

import com.my.foody.domain.socialAccount.dto.resp.LinkageTokenRespDto;
import com.my.foody.domain.socialAccount.dto.resp.SocialAccountListRespDto;
import com.my.foody.domain.socialAccount.dto.resp.SocialLoginRespDto;
import com.my.foody.domain.socialAccount.entity.SocialAccount;
import com.my.foody.domain.socialAccount.service.AccountLinkageService;
import com.my.foody.domain.socialAccount.service.SocialAccountService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SocialAccountController {

    private final AccountLinkageService linkageService;
    private final SocialAccountService socialAccountService;

    //(이미 서버에 인증된 상태로 들어온 상황에서) 계정 연동 시작 -> 임시 토큰 생성
    @RequireAuth(userType = UserType.USER)
    @PostMapping("/api/account/link")
    public ResponseEntity<ApiResult<LinkageTokenRespDto>> startAccountLinkage(@RequestParam(value = "provider")String provider, @CurrentUser TokenSubject tokenSubject){
        return new ResponseEntity<>(ApiResult.success(linkageService.createLinkageToken(tokenSubject.getId(), provider)), HttpStatus.OK);
    }

    // 소셜 로그인 연동 시작 -> 인증 화면으로 갔다가 -> 콜백으로 연결
    @GetMapping("/oauth2/authorization/{provider}")
    public void socialLogin(@PathVariable(value = "provider")String provider,
                            @RequestParam(required = false, value = "linkageToken") String linkageToken,
                            HttpServletResponse response) throws IOException {
        String authorizationUrl = socialAccountService.getAuthorizationUrl(provider, linkageToken);
        response.sendRedirect(authorizationUrl);
    }

    // 소셜 로그인 콜백
    @GetMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<ApiResult<SocialLoginRespDto>> callback(@PathVariable(value = "provider")String provider,
                                                                  @RequestParam(value = "code")String code,
                                                                  @RequestParam(required = false, value = "state")String state,
                                                                  HttpServletResponse response){
        //state(임시토큰) 값이 있으면 추가 소셜 계정 연동
        if(state != null){
            SocialLoginRespDto socialLoginRespDto = socialAccountService.processAccountLinkage(provider, code, state);
            response.setHeader(JwtVo.HEADER, socialLoginRespDto.getToken());
            return new ResponseEntity<>(ApiResult.success(socialLoginRespDto), HttpStatus.OK);
        }
        //일반 로그인 모드
        SocialLoginRespDto socialLoginRespDto = socialAccountService.processOAuth2Callback(provider, code);
        response.setHeader(JwtVo.HEADER, socialLoginRespDto.getToken());
        return new ResponseEntity<>(ApiResult.success(socialLoginRespDto), HttpStatus.OK);
    }

    @GetMapping("/api/users/mypage/social-accounts")
    @RequireAuth(userType = UserType.USER)
    public ResponseEntity<ApiResult<SocialAccountListRespDto>> getAllSocialAccount(@CurrentUser TokenSubject tokenSubject){
        return new ResponseEntity<>(ApiResult.success(socialAccountService.getAllSocialAccount(tokenSubject.getId())), HttpStatus.OK);
    }

}
