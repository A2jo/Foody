package com.my.foody.infra.oauth.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OAuth2Provider {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private List<String> scopes;
}
