package com.my.foody.domain.user.dto.req.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if(password == null){
            return false;
        }
        context.disableDefaultConstraintViolation();
        List<String> errorMsg = validate(password);
        if(!errorMsg.isEmpty()){
            errorMsg.forEach(msg ->
                    context.buildConstraintViolationWithTemplate(msg)
                    .addConstraintViolation());
            return false;
        }
        return true;
    }

    private List<String> validate(String password){
        List<String> errors = new ArrayList<>();

        if (password.length() < 8 || password.length() > 16) {
            errors.add("비밀번호는 8~16자리여야 합니다");
        }

        if (!password.matches(".*[A-Za-z].*")) {
            errors.add("영문자를 최소 1자 이상 포함해야 합니다");
        }

        if (!password.matches(".*\\d.*")) {
            errors.add("숫자를 최소 1자 이상 포함해야 합니다");
        }

        if (!password.matches(".*[@$!%*#?&].*")) {
            errors.add("특수문자를 최소 1자 이상 포함해야 합니다");
        }

        return errors;
    }

}
