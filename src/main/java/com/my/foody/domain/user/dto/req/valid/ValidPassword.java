package com.my.foody.domain.user.dto.req.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "유효하지 않은 비밀번호입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
