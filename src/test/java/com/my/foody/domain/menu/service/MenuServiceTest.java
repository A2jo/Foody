package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.dto.req.MenuCreateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuCreateRespDto;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class MenuServiceTest {
    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    private Store store;
    private Owner owner;
    private MenuCreateReqDto menuCreateReqDto;

    @BeforeEach
    public void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        // 가게 소유자 생성
        owner = Owner.builder()
                .id(1L)
                .email("owner@example.com")
                .password("password123")
                .contact("010-1234-5678")
                .name("OwnerName")
                .build();

        // 가게 생성
        store = Store.builder()
                .id(1L)
                .name("Test Store")
                .owner(owner) // 가게 소유자 지정
                .description("Test Store Description")
                .contact("010-9876-5432")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)
                .build();

        // 메뉴 등록 요청 DTO 생성
        menuCreateReqDto = new MenuCreateReqDto("샐러드", 10000L);
    }

    @Test
    @DisplayName("가게메뉴 등록_성공")
    public void testCreateMenu_Success() {
        // given: 가게가 존재하고 소유자가 일치하는 상황 설정
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        // when: 메뉴 등록 호출
        MenuCreateRespDto response = menuService.createMenu(1L, menuCreateReqDto, owner.getId());

        // then: 결과 검증
        // 응답이 null이 아님을 확인
        assertNotNull(response);

        System.out.println("메뉴등록 메세지 나오면 성공: " + response.getMessage());
    }

    @Test
    @DisplayName("가게메뉴 등록 실패_ 가게없음")
    public void testCreateMenu_StoreNotFound_Failure() {
        // given: 가게가 존재하지 않는 상황 설정
        when(storeRepository.findById(1L)).thenReturn(Optional.empty()); // 가게가 없음을 반환

        // when: 메뉴 등록 호출 및 예외 발생 기대
        assertThrows(BusinessException.class, () -> {
            menuService.createMenu(1L, menuCreateReqDto, owner.getId());
        });

        System.out.println("가게 존재 하지 않음: STORE_NOT_FOUND");
    }

    @Test
    @DisplayName("가게메뉴 등록 실패_ 가게소유자아님")
    public void testCreateMenu_OwnerMismatch_Failure() {
        // given: 가게가 존재하고 다른 소유자가 요청하는 상황 설정
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store)); // 가게 조회 모의

        // when: 메뉴 등록 호출 시 잘못된 소유자 ID 사용
        Long wrongOwnerId = 2L; // 잘못된 소유자 ID

        // 예외 발생 기대
        assertThrows(BusinessException.class, () -> {
            menuService.createMenu(1L, menuCreateReqDto, wrongOwnerId);
        });

        System.out.println("가게소유자아님: FORBIDDEN_ACCESS");
    }

}

