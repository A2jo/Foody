package com.my.foody.domain.socialAccount.dto.resp;

import com.my.foody.domain.socialAccount.entity.SocialAccount;
import com.my.foody.domain.user.entity.Provider;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class SocialAccountListRespDto {

    private List<SocialAccountRespDto> socialAccountList;g 
    private int totalCount;

    public SocialAccountListRespDto(List<SocialAccount> socialAccountList) {
        this.socialAccountList = socialAccountList.stream()
                .map(SocialAccountRespDto::new)
                .toList();
        this.totalCount = socialAccountList.size();
    }

    @NoArgsConstructor
    @Getter
    public static class SocialAccountRespDto{
        public SocialAccountRespDto(SocialAccount socialAccount) {
            this.socialAccountId = socialAccount.getId();
            this.provider = socialAccount.getProvider();
            this.email = socialAccount.getEmail();
        }

        private Long socialAccountId;
        private Provider provider;
        private String email;
    }
}
