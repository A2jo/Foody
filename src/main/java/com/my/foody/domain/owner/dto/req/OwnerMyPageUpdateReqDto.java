package com.my.foody.domain.owner.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerMyPageUpdateReqDto {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Size(max = 100, message = "이름은 최대 100자까지 가능합니다.")
    private String name;

    @Size(max = 15, message = "전화번호는 최대 15자까지 가능합니다.")
    private String contact;

    private String currentPassword;

    @Size(min = 8, max = 60, message = "새 비밀번호는 8자 이상, 60자 이하로 설정해야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "새 비밀번호는 최소 8자, 하나 이상의 문자와 숫자를 포함해야 합니다.")
    private String newPassword;
}
