package com.my.foody.global.handler;

import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.util.api.ApiError;
import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> queryParameterValidationException(ConstraintViolationException e) {
        Map<String, String> errorMap = new HashMap<>();
        e.getConstraintViolations().forEach(error ->
                errorMap.put(((PathImpl) (error.getPropertyPath())).getLeafNode().getName(), error.getMessage()));
        return new ResponseEntity<>(ApiResult.error("유효성 검사 실패",HttpStatus.BAD_REQUEST.value(),  errorMap), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<ApiError>> handleGeneralException(Exception e){
        log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error("서버 에러가 발생했습니다. 잠시 후 다시 시도해주세요", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<ApiError>> handleBusinessException(BusinessException e){
        return new ResponseEntity<>(ApiResult.error(e.getErrorCode().getMsg(), e.getErrorCode().getStatus()), HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e){
        Map<String, String> errorMap = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(ApiResult.error("유효성 검사 실패", HttpStatus.BAD_REQUEST.value(), errorMap), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResult<ApiError>> handleNoResourceFoundException(NoResourceFoundException e){
        return new ResponseEntity<>(ApiResult.error("요청한 URI를 찾을 수 없습니다", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }
}
