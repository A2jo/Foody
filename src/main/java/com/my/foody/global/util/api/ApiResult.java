package com.my.foody.global.util.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "data", "apiError"})
public class ApiResult<T> {
    private T data;
    private boolean success;
    private ApiError apiError;

    public static <T>ApiResult <T> success(T data){
        return new ApiResult<>(data, true, null);
    }
    public static <T>ApiResult<T> error(String msg, int status){
        return new ApiResult<>(null, false, new ApiError(status, msg));
    }
    public static <T>ApiResult<T> error(String msg, int status, T errorMap){
        return new ApiResult<>(errorMap, false, new ApiError(status, msg));
    }
}
