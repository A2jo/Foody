package com.my.foody.infra.oauth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.my.foody.infra.oauth.dto.TokenRespDto;

public class KakaoTokenRespDto implements TokenRespDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_token_expires_in")
    private Integer refreshTokenExpiresIn;

    //허용된 권한
    @JsonProperty("scope")
    private String scope;

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }
}
