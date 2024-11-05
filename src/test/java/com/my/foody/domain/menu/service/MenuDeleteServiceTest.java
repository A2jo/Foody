package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.dto.resp.MenuDeleteRespDto;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MenuDeleteServiceTest {
    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    private Store store;
    private Owner owner;
    private Menu menu;

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

        // 메뉴 생성
        menu = Menu.builder()
                .id(1L)
                .name("기존메뉴이름")
                .price(1000L)
                .build();
    }

    @Test
    @DisplayName("메뉴삭제 성공")
    public void testDeleteMenu_Success() {
        Long menuId = store.getId();
        Long storeId = menu.getId();
        Long ownerId = owner.getId();

        // given: 가게가 존재하고 소유자가 일치
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // when: 메뉴 수정 호출
        MenuDeleteRespDto response = menuService.SoftDeleteMenu(storeId, menuId, ownerId);

        // then: 결과 검증
        // isDeleted가 true로 설정 되었는지 확인
        assertTrue(menu.getIsDeleted());
        assertEquals("메뉴 삭제 되었습니다", response.getMessage());
    }

    @Test
    @DisplayName("메뉴삭제 실패 메뉴가 존재하지 않을 때")
    public void testSoftDeleteMenu_Fail() {
        Long storeId = store.getId();
        Long menuId = menu.getId();
        Long ownerId = owner.getId();

        // given: 가게는 존재하나, 메뉴가 존재하지 않는 상황
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // when, then: MENU_NOT_FOUND 예외 발생 확인
        BusinessException exception = assertThrows(BusinessException.class, () ->
                menuService.SoftDeleteMenu(storeId, menuId, ownerId));
        assertEquals(ErrorCode.MENU_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("메뉴삭제 실패 가게 소유자가 일치하지 않을 때")
    public void testSoftDeleteMenu_ForbiddenAccess() {
        Long storeId = store.getId();
        Long menuId = menu.getId();
        Long incorrectOwnerId = 77L; // 잘못된 소유자 ID

        // given: 가게는 존재하나, 소유자가 일치하지 않는 상황
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when, then: FORBIDDEN_ACCESS 예외 발생 확인
        BusinessException exception = assertThrows(BusinessException.class, () ->
                menuService.SoftDeleteMenu(storeId, menuId, incorrectOwnerId));
        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());
    }

}
