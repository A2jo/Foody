package com.my.foody.domain.owner.dto.resp;

import lombok.Getter;

@Getter
public class OwnerDeleteRespDto {
    public static final OwnerDeleteRespDto INSTANCE = new OwnerDeleteRespDto();
    private final String message = "회원 탈퇴가 완료되었습니다.";

    private OwnerDeleteRespDto() {}
}
