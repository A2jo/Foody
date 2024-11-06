package com.my.foody.domain.search.service;

import com.my.foody.domain.search.dto.req.SearchReqDto;
import com.my.foody.domain.search.dto.resp.SearchRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@MockitoSettings
public class SearchServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private SearchService searchService;

    @Test
    @DisplayName("성공 케이스 - 검색 결과 반환")
    void searchStores_Success() {
        // Given
        SearchReqDto searchReqDto = new SearchReqDto("Store", 0, 2);

        // Store 객체를 Mocking하여 필요한 필드만 설정
        Store store1 = Mockito.mock(Store.class);
        Store store2 = Mockito.mock(Store.class);

        // 필요한 필드만 설정
        when(store1.getId()).thenReturn(1L);
        when(store1.getName()).thenReturn("Store1");
        when(store1.getMinOrderAmount()).thenReturn(1000L);

        when(store2.getId()).thenReturn(2L);
        when(store2.getName()).thenReturn("Store2");
        when(store2.getMinOrderAmount()).thenReturn(2000L);

        when(storeRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(anyString(), any(Pageable.class)))
                .thenReturn(List.of(store1, store2));

        // When
        SearchRespDto response = searchService.searchStores(searchReqDto);

        // Then
        assertEquals(2, response.getStores().size());
        assertEquals("Store1", response.getStores().get(0).getName());
        assertEquals("Store2", response.getStores().get(1).getName());
    }

    @Test
    @DisplayName("실패 케이스 - 검색 결과가 없는 경우 빈 목록 반환")
    void searchStores_NoResults() {
        // Given
        SearchReqDto searchReqDto = new SearchReqDto("NotExist", 0, 2);

        when(storeRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(anyString(), any(Pageable.class)))
                .thenReturn(List.of());

        // When
        SearchRespDto response = searchService.searchStores(searchReqDto);

        // Then
        assertEquals(0, response.getStores().size());
    }
}
