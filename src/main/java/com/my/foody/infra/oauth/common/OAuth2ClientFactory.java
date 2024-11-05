package com.my.foody.infra.oauth.common;

import com.my.foody.infra.oauth.dto.TokenRespDto;
import com.my.foody.infra.oauth.dto.kakao.KakaoTokenRespDto;
import com.my.foody.infra.oauth.kakao.KakaoApiClient;
import com.my.foody.infra.oauth.kakao.KakaoAuthClient;
import com.my.foody.infra.oauth.kakao.KakaoOAuth2Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2ClientFactory {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;
    private final OAuth2Properties oAuth2Properties;

    public OAuth2Client<? extends TokenRespDto> getClient(String provider){
        if(provider.equalsIgnoreCase("kakao")){
            return getKakaoClient();
        }
        throw new IllegalStateException("지원하지 않는 OAuth2 provider 입니다: " + provider);
    }

    public OAuth2Client<KakaoTokenRespDto> getKakaoClient(){
        return new KakaoOAuth2Client(
                kakaoAuthClient,
                kakaoApiClient,
                oAuth2Properties.getProvider("kakao")
        );
    }


}
