package com.my.foody.domain.owner.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerMyPageRespDto {
    private Long id;
    private String name;
    private String contact;
    private String email;

    public OwnerMyPageRespDto(Long id, String name, String contact, String email) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.email = email;
    }
}