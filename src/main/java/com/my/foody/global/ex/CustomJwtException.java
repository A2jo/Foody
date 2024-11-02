package com.my.foody.global.ex;

import lombok.Getter;

@Getter
public class CustomJwtException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
