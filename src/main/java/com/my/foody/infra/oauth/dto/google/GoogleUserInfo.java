package com.my.foody.infra.oauth.dto.google;

import com.my.foody.domain.user.entity.User;
import com.my.foody.infra.oauth.common.OAuth2UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {
    private String id;
    private String email;
    private String name;

    @Override
    public String getProviderId() {
        return id;
    }

    @Override
    public User toEntity() {
        return User.builder()
                .email(getEmail())
                .nickname(getName())
                .name(getName())
                .isDeleted(false)
                .build();
    }
}
