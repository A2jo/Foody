package com.my.foody.domain.user.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
public class UserSignUpReqDto {

    @Email(message = "유효하지 않은 이메일 형식입니다")
    @NotBlank(message = "이메일을 입력해야 합니다")
    private String email;

    @NotBlank(message = "이름을 입력해야 합니다")
    @Length(min = 1, max = 100, message = "이름은 최소 1자 최대 100자로 입력해야 합니다")
    private String name;

    @NotBlank(message = "전화번호를 입력해야 합니다")
    @Length(max = 15, message = "전화번호는 15자 이내여야 합니다")
    private String contact;

    @NotBlank(message = "비밀번호를 입력해야 합니다")
    private String password;

    @NotBlank(message = "닉네임을 입력해야 합니다")
    @Length(min = 1, max = 100, message = "닉네임은 최소 1자 최대 100자로 입력해야 합니다")
    private String nickname;
}
