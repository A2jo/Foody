package com.my.foody.domain.socialAccount.service;

import com.my.foody.domain.socialAccount.dto.resp.SocialAccountListRespDto;
import com.my.foody.domain.socialAccount.entity.SocialAccount;
import com.my.foody.domain.socialAccount.repo.SocialAccountRepository;
import com.my.foody.domain.user.entity.Provider;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialAccountServiceTest {
    @InjectMocks
    private SocialAccountService socialAccountService;

    @Mock
    private UserService userService;
    @Mock
    private SocialAccountRepository socialAccountRepository;

    @Test
    @DisplayName("소셜 계정 목록 조회 성공 테스트")
    void getAllSocialAccount_Success() {
        // given
        Long userId = 1L;
        User user = mock(User.class);

        // 소셜 계정 mock 데이터 생성
        SocialAccount kakaoAccount = mock(SocialAccount.class);
        when(kakaoAccount.getId()).thenReturn(1L);
        when(kakaoAccount.getProvider()).thenReturn(Provider.KAKAO);
        when(kakaoAccount.getEmail()).thenReturn("kakao@test.com");

        SocialAccount googleAccount = mock(SocialAccount.class);
        when(googleAccount.getId()).thenReturn(2L);
        when(googleAccount.getProvider()).thenReturn(Provider.GOOGLE);
        when(googleAccount.getEmail()).thenReturn("google@test.com");

        List<SocialAccount> socialAccounts = List.of(kakaoAccount, googleAccount);

        // mock 설정
        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(socialAccountRepository.findByUser(user)).thenReturn(socialAccounts);

        // when
        SocialAccountListRespDto result = socialAccountService.getAllSocialAccount(userId);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalCount());
        assertEquals(2, result.getSocialAccountList().size());

        // 첫 번째 소셜 계정 검증
        SocialAccountListRespDto.SocialAccountRespDto firstAccount = result.getSocialAccountList().get(0);
        assertEquals(1L, firstAccount.getSocialAccountId());
        assertEquals(Provider.KAKAO, firstAccount.getProvider());
        assertEquals("kakao@test.com", firstAccount.getEmail());

        // 두 번째 소셜 계정 검증
        SocialAccountListRespDto.SocialAccountRespDto secondAccount = result.getSocialAccountList().get(1);
        assertEquals(2L, secondAccount.getSocialAccountId());
        assertEquals(Provider.GOOGLE, secondAccount.getProvider());
        assertEquals("google@test.com", secondAccount.getEmail());

        // 메서드 호출 검증
        verify(userService).findActivateUserByIdOrFail(userId);
        verify(socialAccountRepository).findByUser(user);
    }

    @Test
    @DisplayName("소셜 계정 목록 조회 실패 테스트: 존재하지 않는 사용자")
    void getAllSocialAccount_UserNotFound() {
        // given
        Long userId = 1L;
        when(userService.findActivateUserByIdOrFail(userId))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> socialAccountService.getAllSocialAccount(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(socialAccountRepository, never()).findByUser(any(User.class));
    }

    @Test
    @DisplayName("소셜 계정 목록 조회 성공 테스트: 연동된 소셜 계정이 없는 경우")
    void getAllSocialAccount_EmptyList() {
        // given
        Long userId = 1L;
        User user = mock(User.class);
        List<SocialAccount> emptyList = Collections.emptyList();

        when(userService.findActivateUserByIdOrFail(userId)).thenReturn(user);
        when(socialAccountRepository.findByUser(user)).thenReturn(emptyList);

        // when
        SocialAccountListRespDto result = socialAccountService.getAllSocialAccount(userId);

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
        assertTrue(result.getSocialAccountList().isEmpty());

        verify(userService).findActivateUserByIdOrFail(userId);
        verify(socialAccountRepository).findByUser(user);
    }
}