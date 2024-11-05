package com.my.foody.infra.oauth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.my.foody.domain.user.entity.User;
import com.my.foody.infra.oauth.common.OAuth2UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class KakaoUserInfo implements OAuth2UserInfo {
    private Long id; //회원번호
    @JsonProperty("kakao_account")
    private KakaoAccountDto kakaoAccount;

    @Override
    public String getProviderId() {
        return id.toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getName() {
        return kakaoAccount.getProfile().getNickname();
    }

    @Override
    public User toEntity() {
        return User.builder()
                .email(getEmail())
                .nickname(getName())
                .name(getName())
                .build();
    }
}

