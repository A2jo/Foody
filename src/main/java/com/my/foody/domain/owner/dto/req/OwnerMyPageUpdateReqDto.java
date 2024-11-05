package com.my.foody.domain.owner.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OwnerMyPageUpdateReqDto {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 100, message = "이름은 최대 100자까지 가능합니다.")
    private String name;

    @Size(max = 15, message = "전화번호는 최대 15자까지 가능합니다.")
    private String contact;

    @NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다.")
    private String currentPassword;

    @Size(min = 8, max = 60, message = "새 비밀번호는 8자 이상, 60자 이하로 설정해야 합니다.")
    private String newPassword;
}