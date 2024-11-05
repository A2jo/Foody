package com.my.foody.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.foody.domain.address.dto.req.AddressCreateReqDto;
import com.my.foody.domain.address.dto.resp.AddressCreateRespDto;
import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.user.dto.resp.AddressListRespDto;
import com.my.foody.domain.user.dto.resp.UserInfoRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("마이페이지 조회 실패 테스트: 토큰 없음")
    void getUserInfo_NoToken() throws Exception{
        mvc.perform(get("/api/users/mypage"))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        verify(userService, never()).getUserInfo(any());
    }

    @Test
    @DisplayName("주소지 등록 성공 테스트")
    void registerAddress_Success() throws Exception {
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);
        User user = mockUser();
        Address address = mockAddress(user);
        AddressCreateReqDto addressCreateReqDto = AddressCreateReqDto.builder()
                .roadAddress(address.getRoadAddress())
                .detailedAddress(address.getDetailedAddress())
                .build();
        AddressCreateRespDto result = new AddressCreateRespDto();

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(userService.registerAddress(addressCreateReqDto, userId)).thenReturn(result);

        //when & then
        mvc.perform(post("/api/users/mypage/address")
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token)
                        .content(om.writeValueAsString(addressCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("주소지 등록 실패 테스트: 유효성 검사 실패")
    void registerAddress_Unauthorized() throws Exception {
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);
        User user = mockUser();
        AddressCreateReqDto addressCreateReqDto = AddressCreateReqDto.builder()
                .roadAddress("")
                .detailedAddress("")
                .build();
        when(jwtProvider.validate(token)).thenReturn(tokenSubject);

        // when & then
        mvc.perform(post("/api/users/mypage/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX+token)
                        .content(om.writeValueAsString(addressCreateReqDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(userService, never()).registerAddress(any(), any());
    }

    @Test
    @DisplayName("전체 주소지 조회 성공 테스트: 주소지 크기 5")
    void getAllAddress_Success() throws Exception {
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);
        User user = mockUser();

        List<Address> addressList = new ArrayList<>();
        for(int i = 0;i<5;i++){
            addressList.add(mockAddress(user));
        }

        AddressListRespDto result = new AddressListRespDto(addressList);
        
        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(userService.getAllAddress(userId)).thenReturn(result);

        //when & then
        mvc.perform(get("/api/users/mypage/address")
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.addressList").isArray())
                .andExpect(jsonPath("$.data.addressList", hasSize(addressList.size())))
                .andExpect(jsonPath("$.data.addressList[0].roadAddress").exists())
                .andExpect(jsonPath("$.data.addressList[0].detailedAddress").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("전체 주소지 조회 성공 테스트: 빈 주소 목록")
    void getAllAddress_EmptyList() throws Exception {
        Long userId = 1L;
        String token = "test.token";
        TokenSubject tokenSubject = new TokenSubject(userId, UserType.USER);

        AddressListRespDto result = new AddressListRespDto(new ArrayList<>());

        when(jwtProvider.validate(token)).thenReturn(tokenSubject);
        when(userService.getAllAddress(userId)).thenReturn(result);

        // when & then
        mvc.perform(get("/api/users/mypage/address")
                        .header(JwtVo.HEADER, JwtVo.TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.addressList").isArray())
                .andExpect(jsonPath("$.data.addressList", hasSize(0)))
                .andDo(print());
    }
}
