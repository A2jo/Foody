package com.my.foody.infra.oauth.kakao;

import com.my.foody.infra.oauth.config.OAuthFeignConfig;
import com.my.foody.infra.oauth.dto.kakao.KakaoTokenRespDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoAuthClient", url = "https://kauth.kakao.com/oauth", configuration = OAuthFeignConfig.class)
public interface KakaoAuthClient {

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded;charset=utf-8")
    KakaoTokenRespDto getAccessToken(@RequestParam("client_id") String clientId,
                                     @RequestParam("client_secret") String clientSecret,
                                     @RequestParam("code")String code,
                                     @RequestParam("grant_type")String grantType,
                                     @RequestParam("redirect_uri") String redirectUri);

}
