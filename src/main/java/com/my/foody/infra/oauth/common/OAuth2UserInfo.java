package com.my.foody.infra.oauth.common;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
}
