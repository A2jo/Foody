package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.dto.req.MenuUpdateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuUpdateRespDto;
import com.my.foody.domain.menu.entity.Menu;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MenuUpdateServiceTest {
    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    private Store store;
    private Owner owner;
    private MenuUpdateReqDto menuUpdateReqDto;
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
    @DisplayName("메뉴이름,가격 수정 성공")
    public void testUpdateMenu_ALL_Success() {
        Long menuId = 1L;
        // 메뉴 수정 요청 DTO 생성
        menuUpdateReqDto = new MenuUpdateReqDto("메뉴이름수정", 3000L);

        // given: 가게가 존재하고 소유자가 일치
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // when: 메뉴 수정 호출
        MenuUpdateRespDto response = menuService.updateMenu(1L, menuId, menuUpdateReqDto, owner.getId());

        // then: 결과 검증
        // 응답이 null이 아님을 확인
        assertNotNull(response);
        assertEquals("메뉴이름수정", menu.getName());
        assertEquals(3000L, menu.getPrice());

        System.out.println("메뉴수정 메세지 나오면 성공: " + response.getMessage());
    }

    @Test
    @DisplayName("메뉴 이름만 수정 성공")
    public void testUpdateMenu_Name_Success() {
        Long menuId = 1L;
        // 메뉴 수정 요청 DTO 생성
        menuUpdateReqDto = new MenuUpdateReqDto("수정된 메뉴 이름", null);

        // given: 가게가 존재하고 소유자가 일치
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // when: 메뉴 수정 호출
        MenuUpdateRespDto response = menuService.updateMenu(1L, menuId, menuUpdateReqDto, owner.getId());

        // then: 결과 검증
        // 응답이 null이 아님을 확인
        assertNotNull(response);
        assertEquals("수정된 메뉴 이름", menu.getName());
        assertEquals(1000L, menu.getPrice());

        System.out.println("메뉴수정 메세지 나오면 성공: " + response.getMessage());
    }


    @Test
    @DisplayName("메뉴 가격만 수정 성공")
    public void testUpdateMenu_Price_Success() {
        Long menuId = 1L;
        // 메뉴 수정 요청 DTO 생성
        menuUpdateReqDto = new MenuUpdateReqDto(null, 5000L);

        // given: 가게가 존재하고 소유자가 일치
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // when: 메뉴 수정 호출
        MenuUpdateRespDto response = menuService.updateMenu(1L, menuId, menuUpdateReqDto, owner.getId());

        // then: 결과 검증
        // 응답이 null이 아님을 확인
        assertNotNull(response);
        assertEquals("기존메뉴이름", menu.getName());
        assertEquals(5000L, menu.getPrice());

        System.out.println("메뉴수정 메세지 나오면 성공: " + response.getMessage());
    }

    @Test
    @DisplayName("메뉴이름 유효하지 않는 경우 실패")
    public void testUpdateMenu_Name_Fail() {
        Long menuId = 1L;
        // 메뉴 수정 요청 DTO 생성
        menuUpdateReqDto = new MenuUpdateReqDto("", 1500L);

        // given: 가게가 존재하고 소유자가 일치
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // when & then: 예외 발생 검증
        assertThrows(BusinessException.class, () -> {
            menuService.updateMenu(1L, menuId, menuUpdateReqDto, owner.getId());
        });
    }

}

