package com.my.foody.domain.home;

import com.my.foody.domain.home.controller.HomeController;
import com.my.foody.domain.home.dto.resp.MainHomeRespDto;
import com.my.foody.domain.home.service.HomeService;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.config.AuthInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private AuthInterceptor authInterceptor;

    @Test
    @WithMockUser
    @DisplayName("성공 케이스 - 카테고리 목록 조회 (O 테스트)")
    void getMainHome_Success() throws Exception {
        // AuthInterceptor 모킹 설정: 모든 요청을 허용
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        // 카테고리 목록 설정
        MainHomeRespDto.CategoryDto category1 = new MainHomeRespDto.CategoryDto(1L, "카테고리1");
        MainHomeRespDto.CategoryDto category2 = new MainHomeRespDto.CategoryDto(2L, "카테고리2");
        MainHomeRespDto responseDto = new MainHomeRespDto();
        responseDto.setCategories(List.of(category1, category2));

        when(homeService.getMainHome()).thenReturn(responseDto);

        mockMvc.perform(get("/api/home")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())  // 응답 내용 출력 (디버깅용)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].id").value(1L))
                .andExpect(jsonPath("$.categories[0].name").value("카테고리1"))
                .andExpect(jsonPath("$.categories[1].id").value(2L))
                .andExpect(jsonPath("$.categories[1].name").value("카테고리2"));
    }

    @Test
    @WithMockUser
    @DisplayName("실패 케이스 - 빈 카테고리 목록 반환 (X 테스트)")
    void getMainHome_Failure() throws Exception {
        // AuthInterceptor 모킹 설정: 모든 요청을 허용
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        // 빈 카테고리 목록 설정
        MainHomeRespDto emptyResponseDto = new MainHomeRespDto();
        emptyResponseDto.setCategories(Collections.emptyList());

        when(homeService.getMainHome()).thenReturn(emptyResponseDto);

        mockMvc.perform(get("/api/home")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())  // 응답 내용 출력 (디버깅용)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isEmpty());
    }
}
