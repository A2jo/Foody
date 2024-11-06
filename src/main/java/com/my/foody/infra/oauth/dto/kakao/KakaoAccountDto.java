package com.my.foody.infra.oauth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoAccountDto{

    @JsonProperty("is_email_valid")
    private Boolean isEmailValid; //이메일 유효 여부

    @JsonProperty("is_email_verified")
    private Boolean isEmailVerified; //이메일 인증 여부

    private String email;

    private Profile profile; //프로필 정보 -> 여기에 닉네임 있음


    @NoArgsConstructor
    @Getter
    public static class Profile{
        private String nickname;
    }
}
