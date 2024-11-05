package com.my.foody.domain.store.service;


import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.storeCategory.entity.StoreCategory;
import com.my.foody.domain.storeCategory.repo.StoreCategoryRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private StoreCategoryRepository storeCategoryRepository;
    @Mock
    private Owner owner;

    @DisplayName("가게 생성 성공")
    @Test
    public void testCreateStore_Success() {
        StoreCreateReqDto storeCreateReqDto = new StoreCreateReqDto();
        Long ownerId = 1L;
        storeCreateReqDto.setName("Test Store");
        storeCreateReqDto.setCategoryIds(List.of(1L, 2L));

        Owner owner = mock(Owner.class);
        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(storeRepository.existsByName(storeCreateReqDto.getName())).thenReturn(false);
        when(storeRepository.countByOwnerId(ownerId)).thenReturn(2L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));

        StoreCreateRespDto storeCreateRespDto = storeService.createStore(storeCreateReqDto, ownerId);

        assertNotNull(storeCreateRespDto);
        verify(storeRepository).save(any(Store.class));
        verify(storeCategoryRepository, times(2)).save(any(StoreCategory.class));
    }

    @DisplayName(("실패 테스트 - 존재하지 않는 카테고리 ID"))
    @Test
    public void testCreateStore_Fail_NotFoundCategoryId() {
        StoreCreateReqDto storeCreateReqDto = new StoreCreateReqDto();
        Long ownerId = 1L;
        storeCreateReqDto.setName("Test Store");
        storeCreateReqDto.setCategoryIds(List.of(1L, 999L)); // 존재하지 않는 카테고리 ID

        Owner owner = mock(Owner.class);
        Category category1 = mock(Category.class);

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(storeRepository.existsByName(storeCreateReqDto.getName())).thenReturn(false);
        when(storeRepository.countByOwnerId(ownerId)).thenReturn(2L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.createStore(storeCreateReqDto, ownerId)
        );
        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }

    @DisplayName("실패 테스트 - 동일한 이름")
    @Test
    public void testCreateStore_Fail_DuplicateStoreName() {
        StoreCreateReqDto storeCreateReqDto = new StoreCreateReqDto();
        Long ownerId = 1L;
        storeCreateReqDto.setName("Test Store");
        Owner owner = mock(Owner.class);
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(storeRepository.existsByName(storeCreateReqDto.getName())).thenReturn(true);
        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.createStore(storeCreateReqDto, ownerId)
        );
        assertEquals(ErrorCode.STORENAME_ALREADY_EXISTS, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }

    // 가게 생성 실패 테스트 - 가게 생성 수 제한
    @DisplayName("실패 테스트 - 최대 생성 수 제한")
    @Test
    public void testCreateStore_Fail_MaxStoreLimit() {
        StoreCreateReqDto storeCreateReqDto = new StoreCreateReqDto();
        Long ownerId = 1L;
        storeCreateReqDto.setName("Test Store");
        Owner owner = mock(Owner.class);
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(storeRepository.existsByName(storeCreateReqDto.getName())).thenReturn(false);
        when(storeRepository.countByOwnerId(ownerId)).thenReturn(3L);
        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.createStore(storeCreateReqDto, ownerId)
        );
        assertEquals(ErrorCode.HAVE_FULL_STORE, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }
}
