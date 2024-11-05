package com.my.foody.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.my.foody.domain.user.dto.req.valid.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserDeleteReqDto {

    @ValidPassword
    private String currentPassword;
}
