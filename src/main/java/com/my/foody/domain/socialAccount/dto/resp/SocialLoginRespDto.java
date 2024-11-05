package com.my.foody.domain.socialAccount.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.my.foody.domain.socialAccount.entity.SocialAccount;
import com.my.foody.domain.user.entity.Provider;
import com.my.foody.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SocialLoginRespDto {
    private Long userId;
    private String socialEmail;
    private String socialNickname;
    private Provider provider;
    @JsonIgnore
    private String token;

    public SocialLoginRespDto(User user, SocialAccount socialAccount, String token) {
        this.userId = user.getId();
        this.socialEmail = socialAccount.getEmail();
        this.socialNickname = socialAccount.getNickname();
        this.provider = socialAccount.getProvider();
        this.token = token;
    }
}
