package com.my.foody.global.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다"),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰 서명 입니다"),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 JWT 형식입니다"),
    INVALID_TOKEN_FORMAT(HttpStatus.BAD_REQUEST.value(), "잘못된 JWT 토큰입니다"),
    MISSING_BEARER_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Bearer 인증 정보가 올바르지 않습니다"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다"),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 사용 중인 닉네임입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다"),
    ;

    private final int status;
    private final String msg;

    ErrorCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
