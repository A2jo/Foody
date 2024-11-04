package com.my.foody.domain.owner.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OwnerLoginReqDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
