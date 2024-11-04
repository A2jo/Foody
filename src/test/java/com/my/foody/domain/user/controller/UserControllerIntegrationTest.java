package com.my.foody.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.foody.domain.user.dto.req.UserLoginReqDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerIntegrationTest extends DummyObject {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    MockMvc mvc;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp(){
        User user = mockUser();
        userRepository.save(user);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() throws Exception {
        UserLoginReqDto loginReqDto = UserLoginReqDto.builder()
                .email(mockUser().getEmail())
                .password("Maeda1234!")
                .build();

        ResultActions resultActions = mvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(loginReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
    }

    @Test
    @DisplayName("로그인 실패 테스트: 유효성 검사 실패")
    void login_ValidationFail() throws Exception{
        UserLoginReqDto loginReqDto = UserLoginReqDto.builder()
                .email("a")
                .password("a1")
                .build();

        ResultActions resultActions = mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginReqDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }




}