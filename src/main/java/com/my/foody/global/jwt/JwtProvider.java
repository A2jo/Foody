package com.my.foody.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.user.entity.User;
import com.my.foody.global.ex.CustomJwtException;
import com.my.foody.global.ex.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    public String create(TokenSubject tokenSubject){
        return JwtVo.TOKEN_PREFIX +JWT.create()
                .withSubject("Foody-application")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVo.EXPIRATION_TIME))
                .withClaim("id", tokenSubject.getId())
                .withClaim("userType", tokenSubject.getUserType().name())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public TokenSubject validate(String token){
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        }catch (TokenExpiredException e){
            log.error("토큰 만료: {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.EXPIRED_TOKEN);
        } catch(SignatureVerificationException e){
            log.error("JWT 서명 오류: {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN_SIGNATURE);
        } catch (JWTDecodeException e) {
            log.error("JWT 형식 오류: {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN_TYPE);
        } catch(JWTVerificationException e){
            log.error("JWT 검증 실패: 잘못된 서명 또는 기타 오류 {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN_FORMAT);
        }

        Long id = decodedJWT.getClaim("id").asLong();
        UserType userType = UserType.valueOf(decodedJWT.getClaim("userType").asString());

        return new TokenSubject(id, userType);
    }

}
