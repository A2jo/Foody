package com.my.foody.domain.socialAccount.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.foody.domain.socialAccount.dto.resp.SocialAccountListRespDto;
import com.my.foody.domain.socialAccount.service.AccountLinkageService;
import com.my.foody.domain.socialAccount.service.SocialAccountService;
import com.my.foody.domain.user.controller.UserController;
import com.my.foody.domain.user.entity.Provider;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocialAccountController.class)
@AutoConfigureMockMvc
class SocialAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SocialAccountService socialAccountService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private AccountLinkageService accountLinkageService;

    @Test
    @DisplayName("소셜 계정 목록 조회 성공 테스트")
    void getAllSocialAccount_Success() throws Exception {
        // given
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);


        List<SocialAccountListRespDto.SocialAccountRespDto> accountList = List.of(
                createSocialAccountRespDto(1L, Provider.KAKAO, "kakao@test.com"),
                createSocialAccountRespDto(2L, Provider.GOOGLE, "google@test.com")
        );
        SocialAccountListRespDto response = new SocialAccountListRespDto();
        ReflectionTestUtils.setField(response, "socialAccountList", accountList);
        ReflectionTestUtils.setField(response, "totalCount", 2);

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(socialAccountService.getAllSocialAccount(userId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/users/mypage/social-accounts")
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.socialAccountList").isArray())
                .andExpect(jsonPath("$.data.socialAccountList.length()").value(2))
                .andExpect(jsonPath("$.data.socialAccountList[0].socialAccountId").value(1))
                .andExpect(jsonPath("$.data.socialAccountList[0].provider").value("KAKAO"))
                .andExpect(jsonPath("$.data.socialAccountList[0].email").value("kakao@test.com"))
                .andExpect(jsonPath("$.data.socialAccountList[1].socialAccountId").value(2))
                .andExpect(jsonPath("$.data.socialAccountList[1].provider").value("GOOGLE"))
                .andExpect(jsonPath("$.data.socialAccountList[1].email").value("google@test.com"));

        verify(socialAccountService).getAllSocialAccount(userId);
    }

    @Test
    @DisplayName("소셜 계정 목록 조회 실패 테스트: 존재하지 않는 사용자")
    void getAllSocialAccount_UserNotFound() throws Exception {
        // given
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(socialAccountService.getAllSocialAccount(userId))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/users/mypage/social-accounts")
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.apiError.msg").value("존재하지 않는 유저입니다"));

        verify(socialAccountService).getAllSocialAccount(userId);
    }

    @Test
    @DisplayName("소셜 계정 목록 조회 성공 테스트: 연동된 계정 없음")
    void getAllSocialAccount_EmptyList() throws Exception {
        // given
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);

        SocialAccountListRespDto response = new SocialAccountListRespDto();
        ReflectionTestUtils.setField(response, "socialAccountList", Collections.emptyList());
        ReflectionTestUtils.setField(response, "totalCount", 0);

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(socialAccountService.getAllSocialAccount(userId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/users/mypage/social-accounts")
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(0))
                .andExpect(jsonPath("$.data.socialAccountList").isArray())
                .andExpect(jsonPath("$.data.socialAccountList.length()").value(0));

        verify(socialAccountService).getAllSocialAccount(userId);
    }


    private SocialAccountListRespDto.SocialAccountRespDto createSocialAccountRespDto(
            Long id, Provider provider, String email) {
        SocialAccountListRespDto.SocialAccountRespDto dto = new SocialAccountListRespDto.SocialAccountRespDto();
        ReflectionTestUtils.setField(dto, "socialAccountId", id);
        ReflectionTestUtils.setField(dto, "provider", provider);
        ReflectionTestUtils.setField(dto, "email", email);
        return dto;
    }
}