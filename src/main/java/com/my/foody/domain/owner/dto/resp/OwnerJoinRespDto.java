package com.my.foody.domain.owner.dto.resp;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class OwnerJoinRespDto {

    private final String message = "회원가입 완료 되었습니다."; // DTO에 메시지 기본값 설정

    public OwnerJoinRespDto() {
    }
}