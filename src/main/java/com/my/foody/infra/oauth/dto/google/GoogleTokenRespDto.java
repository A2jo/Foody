package com.my.foody.infra.oauth.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.my.foody.infra.oauth.dto.TokenRespDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GoogleTokenRespDto implements TokenRespDto {

    //google api 호출 시 사용하는 토큰
    @JsonProperty("access_token")
    private String accessToken;

    //토큰 만료 시간
    @JsonProperty("expires_in")
    private Integer expiresIn;

    //Bearer 타입
    @JsonProperty("token_type")
    private String tokenType;

    //허용된 권한
    @JsonProperty("scope")
    private String scope;
}
