package com.my.foody.infra.oauth.common;

public interface OAuth2Client<T> {
    T getAccessToken(String code);
    OAuth2UserInfo getUserInfo(String accessToken);
}
