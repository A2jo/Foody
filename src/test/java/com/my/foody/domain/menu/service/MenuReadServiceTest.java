package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.dto.resp.MenuReadResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MenuReadServiceTest {
    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    private Store store1, store2;
    private Owner owner;
    private Menu menu1, menu2;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        // 가게 소유자 생성
        owner = Owner.builder()
                .id(1L)
                .email("owner@example.com")
                .password("password123!")
                .contact("010-1234-5678")
                .name("OwnerName")
                .build();

        // 가게 생성
        store1 = Store.builder()
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

        store2 = Store.builder()
                .id(2L)
                .name("Test Store")
                .owner(owner) // 가게 소유자 지정
                .description("Test Store Description")
                .contact("010-1234-5678")
                .minOrderAmount(20000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)
                .build();

        // 메뉴 생성
        menu1 = Menu.builder()
                .id(1L)
                .name("가게1 메뉴")
                .price(1000L)
                .store(store1)
                .build();

        menu2 = Menu.builder()
                .id(2L)
                .name("가게2 메뉴")
                .price(2000L)
                .store(store2)
                .build();

        //페이징
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("가게1의 메뉴만 조회되는지")
    public void testUpdateMenu_ALL_Success() {
        // given: store1이 존재하고 소유자가 일치하는 경우
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store1));
        when(menuRepository.findAllByStoreId(1L, pageable)).thenReturn(new PageImpl<>(List.of(menu1), pageable, 1));

        // when: 가게1의 메뉴 조회 호출
        Page<MenuReadResponseDto> response = menuService.getMenus(1L, 1L, 0,10);

        // then: 결과 검증 - store1의 메뉴만 반환되었는지 확인
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("가게1 메뉴", response.getContent().get(0).getName());
        assertEquals(1000L, response.getContent().get(0).getPrice());

        // verify: 메서드 호출 검증
        verify(storeRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).findAllByStoreId(1L, pageable);
    }

    @Test
    @DisplayName("가게가 존재하지 않는 경우 예외 발생 테스트")
    public void testGetMenus_StoreNotFound() {
        // given: 가게가 존재하지 않는 경우 (존재하지 않는 storeId 사용)
        when(storeRepository.findById(4L)).thenReturn(Optional.empty()); // 실제로 없는 ID 4L

        // when & then: 예외가 발생하는지 확인
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            menuService.getMenus(4L, 1L, 0,10); // storeId 4L 사용
        });

        // 예외처리 메세지
        System.out.println("발생한 예외 메세지: " + exception.getMessage());

        assertEquals(ErrorCode.STORE_NOT_FOUND, exception.getErrorCode());

        // verify: 메서드 호출 검증
        verify(storeRepository, times(1)).findById(4L);
        verify(menuRepository, never()).findAllByStoreId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("가게 소유자가 아닌 경우 예외 발생 테스트")
    public void testGetMenus_NotStoreOwner() {
        // given: 가게가 존재하고 소유자 확인
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store1));

        // ownerId가 다르면 예외 발생
        Long nonOwnerId = 2L; // 가게 소유자와 다른 ID

        // when & then: 예외가 발생하는지 확인
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            menuService.getMenus(1L, nonOwnerId, 0,10);
        });

        // 예외 메시지 검증
        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());

        // verify: 메서드 호출 검증
        verify(storeRepository, times(1)).findById(1L);
        verify(menuRepository, never()).findAllByStoreId(anyLong(), any(Pageable.class));
    }

}
