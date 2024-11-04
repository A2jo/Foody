package com.my.foody.global.config;

import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class TokenSubjectArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(TokenSubject.class) && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Long id = (Long) request.getAttribute("id");
        UserType userType = (UserType) request.getAttribute("userType");

        if(id == null || userType == null){
            log.warn("유효하지 않은 토큰을 사용한 접근 시도 발생");
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        return new TokenSubject(id, userType);
    }
}
