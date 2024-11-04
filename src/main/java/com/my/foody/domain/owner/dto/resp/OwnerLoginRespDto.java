package com.my.foody.domain.owner.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerLoginRespDto {

    private String token;
    private String message;

    public OwnerLoginRespDto(String token, String message) {
        this.token = token;
        this.message = message;
    }
}