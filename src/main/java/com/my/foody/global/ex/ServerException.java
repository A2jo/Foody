package com.my.foody.global.ex;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException{
    private final ErrorCode errorCode;

    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}
