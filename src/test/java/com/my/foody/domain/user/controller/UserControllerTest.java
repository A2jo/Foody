package com.my.foody.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.foody.domain.user.dto.resp.UserInfoRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("마이페이지 조회 성공 테스트")
    void getUserInfo_Success() throws Exception {
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);
        User user = mockUser();

        UserInfoRespDto result = new UserInfoRespDto(user);

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(userService.getUserInfo(userId)).thenReturn(result);

        //when & then
        mvc.perform(get("/api/users/mypage")
                .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(user.getName()))
                .andExpect(jsonPath("$.data.email").value(user.getEmail()))
                .andDo(print());
    }
}
