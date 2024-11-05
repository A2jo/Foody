package com.my.foody.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.my.foody.domain.user.dto.req.valid.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class UserDeleteReqDto {

    @ValidPassword
    private String currentPassword;
}
