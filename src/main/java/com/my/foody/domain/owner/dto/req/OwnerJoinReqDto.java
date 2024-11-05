package com.my.foody.domain.owner.dto.req;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class OwnerJoinReqDto {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 100, message = "이름은 최대 100자까지 가능합니다.")
    private String name;

    @Size(max = 15, message = "전화번호는 최대 15자까지 가능합니다.")
    private String contact;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 60, message = "비밀번호는 8자 이상, 60자 이하로 설정해야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "는 최소 8자, 하나 이상의 문자와 숫자를 포함해야 합니다.")
    private String password;

    @NotNull(message = "삭제 여부는 필수 입력 항목입니다.")
    private Boolean isDeleted;
}
