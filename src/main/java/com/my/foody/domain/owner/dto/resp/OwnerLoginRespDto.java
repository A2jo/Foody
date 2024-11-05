package com.my.foody.domain.owner.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class OwnerLoginRespDto {

    private final String token;
    private final String message = "로그인 성공"; // DTO에 메시지 기본값 설정

    public OwnerLoginRespDto(String token) {
        this.token = token;
    }
}