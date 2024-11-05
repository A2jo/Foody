package com.my.foody.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.my.foody.domain.user.dto.req.valid.ValidPassword;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class UserPasswordModifyReqDto {

    @ValidPassword
    private String currentPassword;

    @ValidPassword
    private String newPassword;

    @AssertTrue(message = "새로운 비밀번호는 현재 비밀번호와 달라야 합니다")
    public boolean isPasswordDifferent(){
        if(currentPassword != null || newPassword != null){
            return true;
        }
        return !currentPassword.equals(newPassword);
    }

}
