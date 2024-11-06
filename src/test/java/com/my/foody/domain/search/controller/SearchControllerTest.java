package com.my.foody.domain.search.controller;

import com.my.foody.domain.search.dto.req.SearchReqDto;
import com.my.foody.domain.search.dto.resp.SearchRespDto;
import com.my.foody.domain.search.service.SearchService;
import com.my.foody.global.config.AuthInterceptor;
import com.my.foody.global.jwt.JwtProvider;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private AuthInterceptor authInterceptor;

    @Test
    @WithMockUser
    @DisplayName("성공 케이스 - 검색 결과 반환")
    void searchStores_Success() throws Exception {
        // AuthInterceptor 모킹 설정
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        // Given
        SearchRespDto.StoreDto store1 = new SearchRespDto.StoreDto(1L, "Store1", 1000L);
        SearchRespDto.StoreDto store2 = new SearchRespDto.StoreDto(2L, "Store2", 2000L);
        SearchRespDto responseDto = new SearchRespDto(List.of(store1, store2));

        when(searchService.searchStores(any(SearchReqDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/search")
                        .param("keyword", "Store")
                        .param("page", "0")
                        .param("limit", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores[0].id").value(1L))
                .andExpect(jsonPath("$.stores[0].name").value("Store1"))
                .andExpect(jsonPath("$.stores[0].minOrderAmount").value(1000L))
                .andExpect(jsonPath("$.stores[1].id").value(2L))
                .andExpect(jsonPath("$.stores[1].name").value("Store2"))
                .andExpect(jsonPath("$.stores[1].minOrderAmount").value(2000L));
    }

    @Test
    @WithMockUser
    @DisplayName("실패 케이스 - 검색 결과가 없는 경우 빈 목록 반환")
    void searchStores_NoResults() throws Exception {
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        // Given
        SearchRespDto emptyResponseDto = new SearchRespDto(Collections.emptyList());
        when(searchService.searchStores(any(SearchReqDto.class))).thenReturn(emptyResponseDto);

        // When & Then
        mockMvc.perform(get("/api/search")
                        .param("keyword", "NonExistentStore")
                        .param("page", "0")
                        .param("limit", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("실패 케이스 - 유효하지 않은 파라미터로 요청 시 잘못된 요청 반환")
    void searchStores_InvalidParameters() throws Exception {

        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        mockMvc.perform(get("/api/search")
                        // 빈 키워드
                        .param("keyword", "")
                        // 유효하지 않은 페이지 번호
                        .param("page", "-1")
                        // 유효하지 않은 페이지 크기
                        .param("limit", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
