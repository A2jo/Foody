package com.my.foody.infra.oauth.kakao;


import com.my.foody.infra.oauth.config.OAuthFeignConfig;
import com.my.foody.infra.oauth.dto.kakao.KakaoUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com", configuration = OAuthFeignConfig.class)
public interface KakaoApiClient {
    @GetMapping(value = "/v2/user/me", consumes = "application/x-www-form-urlencoded;charset=utf-8")
    KakaoUserInfo getUserInfo(@RequestHeader("Authorization")String token);

}
