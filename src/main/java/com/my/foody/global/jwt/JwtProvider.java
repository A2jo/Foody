package com.my.foody.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.my.foody.global.ex.CustomJwtException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    private final RedisTemplate<String, String> redisTemplate;
    private final static String LOGOUT_KEY = "BL:";

    // 블랙리스트로 사용할 Map 추가
    private final ConcurrentHashMap<String, Date> tokenBlacklist = new ConcurrentHashMap<>();

    public String create(TokenSubject tokenSubject){
        Date now = new Date();
        return JwtVo.TOKEN_PREFIX + JWT.create()
                .withSubject("Foody-application")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVo.EXPIRATION_TIME))
                .withIssuedAt(now)
                .withClaim("id", tokenSubject.getId())
                .withClaim("userType", tokenSubject.getUserType().name())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public TokenSubject validate(String token){
        String key = LOGOUT_KEY + token.replace(JwtVo.TOKEN_PREFIX, "");
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN);
        }

        DecodedJWT decodedJWT;
        try {
            // 블랙리스트에 있는지 확인
            if (tokenBlacklist.containsKey(token)) {
                throw new CustomJwtException(ErrorCode.INVALID_TOKEN_FORMAT);
            }

            decodedJWT = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        } catch (TokenExpiredException e) {
            log.error("토큰 만료: {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.EXPIRED_TOKEN);
        } catch (SignatureVerificationException e) {
            log.error("JWT 서명 오류: {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN_SIGNATURE);
        } catch (JWTDecodeException e) {
            log.error("JWT 형식 오류: {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN_TYPE);
        } catch (JWTVerificationException e) {
            log.error("JWT 검증 실패: 잘못된 서명 또는 기타 오류 {}", e.getMessage(), e);
            throw new CustomJwtException(ErrorCode.INVALID_TOKEN_FORMAT);
        }

        Long id = decodedJWT.getClaim("id").asLong();
        UserType userType = UserType.valueOf(decodedJWT.getClaim("userType").asString());

        return new TokenSubject(id, userType);
    }

    // 토큰 무효화 메서드 추가
    public void logout(Long userId, String token) {
        String key = LOGOUT_KEY + token.replace(JwtVo.TOKEN_PREFIX, "");
        DecodedJWT jwt = JWT.decode(token.replace(JwtVo.TOKEN_PREFIX, ""));
        long remainingTime = jwt.getExpiresAt().getTime() - System.currentTimeMillis();

        redisTemplate.opsForValue().set(key, userId.toString(), remainingTime, TimeUnit.MILLISECONDS);
    }
}
