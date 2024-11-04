package com.my.foody.global.ex;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ServerException extends RuntimeException {
    private final ErrorCode errorCode;

    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    private void logError() {
        log.error("CustomJwtException 발생 - Code: {}, Message: {}", errorCode, errorCode.getMsg());
    }
}