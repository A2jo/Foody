package com.my.foody.infra.oauth.google;

import com.my.foody.infra.oauth.config.OAuthFeignConfig;
import com.my.foody.infra.oauth.dto.google.GoogleUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleApiClient", url = "https://www.googleapis.com", configuration = OAuthFeignConfig.class)
public interface GoogleApiClient {

    @GetMapping("/oauth2/v2/userinfo")
    GoogleUserInfo getUserInfo(@RequestHeader("Authorization") String token);



}
