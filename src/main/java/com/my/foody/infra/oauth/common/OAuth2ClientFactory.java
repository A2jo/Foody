package com.my.foody.infra.oauth.common;

import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.infra.oauth.dto.TokenRespDto;
import com.my.foody.infra.oauth.dto.google.GoogleTokenRespDto;
import com.my.foody.infra.oauth.dto.kakao.KakaoTokenRespDto;
import com.my.foody.infra.oauth.google.GoogleApiClient;
import com.my.foody.infra.oauth.google.GoogleAuthClient;
import com.my.foody.infra.oauth.google.GoogleOAuth2Client;
import com.my.foody.infra.oauth.kakao.KakaoApiClient;
import com.my.foody.infra.oauth.kakao.KakaoAuthClient;
import com.my.foody.infra.oauth.kakao.KakaoOAuth2Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2ClientFactory {

    private final GoogleAuthClient googleAuthClient;
    private final GoogleApiClient googleApiClient;

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;
    private final OAuth2Properties oAuth2Properties;

    public OAuth2Client<? extends TokenRespDto> getClient(String provider){
        if(provider.equalsIgnoreCase("kakao")){
            return getKakaoClient();
        }
        else if(provider.equalsIgnoreCase("google")){
            return getGoogleClient();
        }
        throw new BusinessException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
    }

    public OAuth2Client<KakaoTokenRespDto> getKakaoClient(){
        return new KakaoOAuth2Client(
                kakaoAuthClient,
                kakaoApiClient,
                oAuth2Properties.getProvider("kakao")
        );
    }

    public OAuth2Client<GoogleTokenRespDto> getGoogleClient() {
        return new GoogleOAuth2Client(
                googleAuthClient,
                googleApiClient,
                oAuth2Properties.getProvider("google")
        );
    }


}
