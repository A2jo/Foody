package com.my.foody.domain.socialAccount.service;

import com.my.foody.domain.socialAccount.dto.resp.SocialLoginRespDto;
import com.my.foody.domain.socialAccount.entity.SocialAccount;
import com.my.foody.domain.socialAccount.repo.SocialAccountRepository;
import com.my.foody.domain.user.dto.req.UserLoginReqDto;
import com.my.foody.domain.user.entity.Provider;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.util.api.ApiResult;
import com.my.foody.infra.oauth.common.*;
import com.my.foody.infra.oauth.dto.TokenRespDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class SocialAccountService {

    private final OAuth2Properties oAuth2Properties;
    private final OAuth2ClientFactory oAuth2ClientFactory;
    private final UserService userService;
    private final AccountLinkageService linkageService;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String RESPONSE_TYPE = "code";
    public String getAuthorizationUrl(String provider, String linkageToken) {
        OAuth2Provider oAuth2Provider = oAuth2Properties.getProvider(provider);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(oAuth2Provider.getAuthorizationUri())
                .queryParam("client_id", oAuth2Provider.getClientId())
                .queryParam("redirect_uri", oAuth2Provider.getRedirectUri())
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("scope", String.join(" ", oAuth2Provider.getScopes()));

        if(linkageToken != null){
            builder.queryParam("state", linkageToken);
        }
        return builder.build().toUriString();
    }

    //계정 연동 처리
    public SocialLoginRespDto processAccountLinkage(String provider, String code, String linkageToken) {
        try {
            //토큰으로 기존 사용자 확인
            Long userId = linkageService.getUserIdFromToken(linkageToken);

            //oauth2 인증 처리
            OAuth2Client<? extends TokenRespDto> client = oAuth2ClientFactory.getClient(provider);
            TokenRespDto tokenRespDto = client.getAccessToken(code);
            OAuth2UserInfo userInfo = client.getUserInfo(tokenRespDto.getAccessToken());

            //계정 연동 처리
            return userService.linkAccount(userId, userInfo, Provider.fromString(provider));
        } catch (FeignException e) {
            log.error("OAuth2 계정 연동 프로세스 실패 - provider: {}, linkageToken: {}, error: {}",
                    provider, linkageToken, e.getMessage());
            throw new IllegalStateException("OAuth2 계정 연동 실패: {}" + e.getMessage(), e);
        }
    }

    //일반 로그인 처리
    public SocialLoginRespDto processOAuth2Callback(String provider, String code){
        OAuth2Client<? extends TokenRespDto> client = oAuth2ClientFactory.getClient(provider);
        try{
            TokenRespDto tokenRespDto = client.getAccessToken(code);
            OAuth2UserInfo userInfo = client.getUserInfo(tokenRespDto.getAccessToken());
            // 소셜 계정 존재 여부 확인
            try {
                // 기존 소셜 계정으로 로그인 시도
                return loginWithSocialAccount(userInfo, Provider.fromString(provider));
            } catch (BusinessException e) {
                if (e.getErrorCode() == ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND) { //소셜 계정이 없는 경우 새로 가입
                    return registerWithSocialAccount(userInfo, Provider.fromString(provider));
                }
                throw e;
            }
        }catch (FeignException e){
            log.error("OAuth2 프로세스 실패 - provider: {}, error: {}", provider, e.getMessage());
            throw new IllegalStateException("OAuth2 콜백 실패: {}"+e.getMessage(), e);
        }
    }

    //소셜 계정을 통한 로그인
    public SocialLoginRespDto loginWithSocialAccount(OAuth2UserInfo userInfo, Provider provider) {
        //소셜 계정 정보로 기존 연동 정보 조회
        SocialAccount socialAccount = socialAccountRepository.findByProviderAndProviderId(provider, userInfo.getProviderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));
        User user = userService.findActivateUserByIdOrFail(socialAccount.getUser().getId());

        if (user.getIsDeleted()) {
            throw new BusinessException(ErrorCode.ALREADY_DEACTIVATED_USER);
        }

        String token = jwtProvider.create(TokenSubject.of(user));
        return new SocialLoginRespDto(user, socialAccount, token);
    }

    // 소셜 계정으로 최초 가입 (소셜 계정이 없는 경우)
    public SocialLoginRespDto registerWithSocialAccount(OAuth2UserInfo userInfo, Provider provider) {

        User user = userInfo.toEntity();
        userRepository.save(user);

        //SocialAccount 생성 및 연동
        SocialAccount socialAccount = createSocialAccount(user, provider, userInfo);
        socialAccountRepository.save(socialAccount);

        String token = jwtProvider.create(TokenSubject.of(user));
        return new SocialLoginRespDto(user, socialAccount, token);
    }

    public SocialAccount createSocialAccount(User user, Provider provider, OAuth2UserInfo userInfo){
        return SocialAccount.builder()
                .user(user)
                .providerId(userInfo.getProviderId())
                .provider(provider)
                .email(userInfo.getEmail())
                .nickname(userInfo.getName())
                .name(userInfo.getName())
                .build();
    }
}
