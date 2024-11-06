package com.my.foody.domain.socialAccount.service;

import com.my.foody.domain.socialAccount.dto.resp.LinkageTokenRespDto;
import com.my.foody.domain.socialAccount.repo.SocialAccountRepository;
import com.my.foody.domain.user.entity.Provider;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AccountLinkageService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TEMPORAL_TOKEN_PREFIX = "link:";
    private static final long TOKEN_VALID_TIME = 5; //5분으로 설정
    private final SocialAccountRepository socialAccountRepository;

    public void validateLinkageToken(String linkageToken){
        String redisKey = TEMPORAL_TOKEN_PREFIX + linkageToken;
        String userId = redisTemplate.opsForValue().get(redisKey);

        if (userId == null) {
            throw new BusinessException(ErrorCode.EXPIRED_LINKAGE_TOKEN);
        }
    }

    public LinkageTokenRespDto createLinkageToken(Long userId, String provider){

        //이미 해당 provider로 연동된 계정이 있는지 확인
        if(socialAccountRepository.existsByProviderAndUserId(Provider.fromString(provider), userId)){
            throw new BusinessException(ErrorCode.ALREADY_LINKED_OAUTH);
        }

        String token = UUID.randomUUID().toString();
        String redisKey = TEMPORAL_TOKEN_PREFIX + token;

        //임시 토큰
        redisTemplate.opsForValue().set(
                redisKey,
                userId.toString(),
                TOKEN_VALID_TIME,
                TimeUnit.MINUTES
        );

        return new LinkageTokenRespDto(token);
    }

    public Long getUserIdFromToken(String token){
        String redisKey = TEMPORAL_TOKEN_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(redisKey);

        if(userId == null){
            throw new BusinessException(ErrorCode.EXPIRED_LINKAGE_TOKEN);
        }
        redisTemplate.delete(redisKey);
        return Long.parseLong(userId);
    }
}
