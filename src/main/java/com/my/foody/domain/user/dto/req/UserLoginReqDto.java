package com.my.foody.domain.user.dto.req;

import com.my.foody.domain.user.dto.req.valid.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class UserLoginReqDto {
    @Email(message = "유효하지 않은 이메일 형식입니다")
    @NotBlank(message = "이메일을 입력해야 합니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력해야 합니다")
    @ValidPassword
    private String password;
}
