package com.my.foody.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.CustomJwtException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final ObjectMapper om;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireAuth requireAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);

        //인증 필요 없는 엔드포인트인 경우 검사 안하고 넘김
        if(requireAuth == null){
            return true;
        }

        try {
            String token = extractToken(request);
            TokenSubject tokenSubject = jwtProvider.validate(token);

            if (!isAllowedUserType(requireAuth.userType(), tokenSubject.getUserType())) {
                log.warn("권한 없는 접근 시도 - 요청된 권한: {}, 사용자 권한: {}, 사용자 ID: {}, URI: {}",
                        requireAuth.userType(),
                        tokenSubject.getUserType(),
                        tokenSubject.getId(),
                        request.getRequestURI()
                );
                throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
            }
            request.setAttribute("id", tokenSubject.getId());
            request.setAttribute("userType", tokenSubject.getUserType());
        }catch(CustomJwtException e){
            sendErrorResponse(response, e.getErrorCode().getStatus(), e.getErrorCode().getMsg());
            return false;
        }catch (BusinessException e){
            sendErrorResponse(response, e.getErrorCode().getStatus(), e.getErrorCode().getMsg());
            return false;
        }
        return true;
    }

    private boolean isAllowedUserType(UserType[] allowedUserType, UserType currentUserType){
        return Arrays.asList(allowedUserType).contains(currentUserType);
    }

    private String extractToken(HttpServletRequest request){
        String bearerToken = request.getHeader(JwtVo.HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtVo.TOKEN_PREFIX)){
            return bearerToken.substring(7);
        }
        throw new CustomJwtException(ErrorCode.MISSING_BEARER_TOKEN);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String msg) throws IOException {
        ApiResult<Object> unAuthResponse = ApiResult.error(msg, status);
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(om.writeValueAsString(unAuthResponse));
    }
}
