package com.my.foody.domain.owner.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerLogoutRespDto {
    private final String message = "로그아웃이 완료되었습니다.";
}