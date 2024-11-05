package com.my.foody.infra.oauth.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {
    private Map<String, OAuth2Provider> providers;

    public OAuth2Provider getProvider(String provider){
        return providers.get(provider);
    }
}
