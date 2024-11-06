package com.my.foody.infra.oauth.common;

import com.my.foody.domain.user.entity.User;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();

    User toEntity();
}
