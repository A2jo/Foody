package com.my.foody.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.my.foody.global.ex.CustomJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private final static String LOGOUT_KEY = "BL:";

    private static final String TEST_SECRET_KEY = "testsecretkeytestsecretkeytestsecretkeytestsecretkey";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtProvider, "secretKey", TEST_SECRET_KEY);
    }


    @Test
    @DisplayName("로그아웃 성공 테스트: 토큰이 Redis에 정상적으로 저장되는지 검증")
    void logout_shouldSaveTokenToRedis() {
        // Given
        Long userId = 1L;
        String actualToken = createTestToken(new TokenSubject(userId, UserType.USER));
        String tokenWithPrefix = JwtVo.TOKEN_PREFIX + actualToken;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        jwtProvider.logout(userId, tokenWithPrefix);

        // Then
        verify(valueOperations).set(
                argThat(key -> key.startsWith(LOGOUT_KEY)),
                eq(userId.toString()),
                anyLong(),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("로그아웃된 토큰으로 검증 시 예외 발생 테스트")
    void validate_withLogoutToken_shouldThrowException() {
        TokenSubject tokenSubject = new TokenSubject(1L, UserType.USER);
        String token = createTestToken(tokenSubject);
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // When & Then
        assertThrows(CustomJwtException.class, () -> {
            jwtProvider.validate(token);
        });
    }

    @Test
    @DisplayName("유효한 토큰 검증 테스트")
    void validate_withValidToken_shouldReturnTokenSubject() {
        // Given
        TokenSubject tokenSubject = new TokenSubject(1L, UserType.USER);
        String token = createTestToken(tokenSubject);  // 순수 토큰 생성
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // When
        TokenSubject result = jwtProvider.validate(token);  // Bearer 없이 호출

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(UserType.USER, result.getUserType());
    }

    private String createTestToken(TokenSubject subject) {
        return JWT.create()
                .withSubject("Foody-application")
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .withIssuedAt(new Date())
                .withClaim("id", subject.getId())
                .withClaim("userType", subject.getUserType().name())
                .sign(Algorithm.HMAC512(TEST_SECRET_KEY));
    }
}