package com.my.foody.domain.owner.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerMyPageUpdateRespDto {

    private Long id;
    private String name;
    private String contact;
    private String email;
}
