package com.my.foody.domain.store.controller;


import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;
import com.my.foody.domain.store.dto.resp.GetStoreRespDto;
import com.my.foody.domain.store.dto.resp.StoreListRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.store.service.StoreService;
import com.my.foody.domain.storeCategory.entity.StoreCategory;
import com.my.foody.domain.storeCategory.repo.StoreCategoryRepository;
import com.my.foody.global.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StoreController.class)
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private StoreRepository storeRepository;
    @MockBean
    private StoreCategoryRepository storeCategoryRepository;

    @BeforeEach
    public void setUp() {
        // Category 객체 생성
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("한식");

        // Store 객체 생성
        Store store = Store.builder()
                .id(1L)
                .name("Test Store")
                .description("This is a test store.")
                .minOrderAmount(10000L)
                .isDeleted(false)
                .build();

        // StoreCategory 객체로 Store와 Category 연결
        StoreCategory storeCategory = StoreCategory.builder()
                .store(store)
                .category(category)
                .build();

        // 서비스 Mock 설정
        GetStoreRespDto storeDto = new GetStoreRespDto(store.getId(), store.getName(), store.getMinOrderAmount());
        List<GetStoreRespDto> storeList = List.of(storeDto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<GetStoreRespDto> storePage = new PageImpl<>(storeList, pageable, storeList.size());
        StoreListRespDto storeListRespDto = new StoreListRespDto(storePage);

        when(storeService.getStoreByCategory(1L, 0, 10)).thenReturn(storeListRespDto);
    }


    @DisplayName("카테고리별 가게 목록 조회 성공 테스트")
    @Test
    public void testGetStoresByCategory_Success() throws Exception {
        Long categoryId = 1L;
        int page = 0;
        int limit = 10;

        // When & Then
        mockMvc.perform(get("/api/home/categories/{categoryId}/store", categoryId)
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.storeList[0].name").value("Test Store"))
                .andExpect(jsonPath("$.data.storeList[0].minOrderAmount").value(10000L))
                .andExpect(jsonPath("$.data.pageInfo.pageNumber").value(page));
    }


}
