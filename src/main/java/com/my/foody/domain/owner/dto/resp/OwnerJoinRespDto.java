package com.my.foody.domain.owner.dto.resp;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerJoinRespDto {

    private String message;
    public OwnerJoinRespDto(String message) {
        this.message = message;
    }

}
