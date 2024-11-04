package com.my.foody.domain.user.dto.resp;

import com.my.foody.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserInfoRespDto {
    private String name;
    private String nickname;
    private String contact;
    private String email;

    public UserInfoRespDto(User user) {
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.contact = user.getContact();
        this.email = user.getEmail();
    }
}
