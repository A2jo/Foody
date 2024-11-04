package com.my.foody.domain.owner.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OwnerJoinReqDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 15)
    private String contact;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(max = 60)
    private String password;

    @NotNull
    private Boolean isDeleted;


}
