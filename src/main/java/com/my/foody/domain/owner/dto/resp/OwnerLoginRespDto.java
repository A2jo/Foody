package com.my.foody.domain.owner.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerLoginRespDto {

    @JsonIgnore
    private String token;
    private String message;

    public OwnerLoginRespDto(String token) {
        this.token = token;
        this.message = "로그인 되었습니다";
    }
}
