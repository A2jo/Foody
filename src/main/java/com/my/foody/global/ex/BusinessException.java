package com.my.foody.global.ex;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
        logError();  // 예외 발생 시 로깅
    }

    private void logError() {
        log.error("BusinessException 발생 - Code: {}, Message: {}", errorCode, errorCode.getMsg());
    }
}
