package com.my.foody.domain.store.service;


import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.store.dto.req.ModifyStoreReqDto;
import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.GetStoreRespDto;
import com.my.foody.domain.store.dto.resp.ModifyStoreRespDto;
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

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
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
        when(storeRepository.countByOwnerIdAndIsDeletedFalse(ownerId)).thenReturn(2L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));

        StoreCreateRespDto storeCreateRespDto = storeService.createStore(storeCreateReqDto, ownerId);

        assertNotNull(storeCreateRespDto);
        assertEquals("가게가 생성되었습니다.", storeCreateRespDto.getMessage());

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
        when(storeRepository.countByOwnerIdAndIsDeletedFalse(ownerId)).thenReturn(3L);
        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.createStore(storeCreateReqDto, ownerId)
        );
        assertEquals(ErrorCode.HAVE_FULL_STORE, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }

    // -[가게 조회 테스트]---------------------------------------------------

    @DisplayName("가게 조회 성공 테스트")
    @Test
    public void testGetAllStore_Success() {
        Long ownerId = 1L;
        Owner owner = mock(Owner.class);

        Store store1 = Store.builder()
                .name("Store 1")
                .owner(owner)
                .description("First Store")
                .contact("010-1111-2222")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)  // 영업 중
                .build();
        Store store2 = Store.builder()
                .name("Store 2")
                .owner(owner)
                .description("Second Store")
                .contact("010-3333-4444")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(23, 0))
                .isDeleted(true)  // 폐업
                .build();
        Store store3 = Store.builder()
                .name("Store 3")
                .owner(owner)
                .description("Third Store")
                .contact("010-5555-6666")
                .minOrderAmount(5000L)
                .openTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(23, 0))
                .isDeleted(false)  // 영업 중
                .build();

        when(storeRepository.findByOwnerId(ownerId)).thenReturn(List.of(store1, store2, store3));

        List<GetStoreRespDto> result = storeService.getAllStoresByOwnerId(ownerId);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("Store 1", result.get(0).getName());
        assertFalse(result.get(0).isDeleted());
        System.out.println("이름 : " + result.get(0).getName() + "\n" + "영업 상태 : " + result.get(0).isDeleted());

        assertEquals("Store 2", result.get(1).getName());
        assertTrue(result.get(1).isDeleted());
        System.out.println("이름 : " + result.get(1).getName() + "\n" + "영업 상태 : " + result.get(1).isDeleted());

        assertEquals("Store 3", result.get(2).getName());
        assertFalse(result.get(2).isDeleted());
        System.out.println("이름 : " + result.get(2).getName() + "\n" + "영업 상태 : " + result.get(2).isDeleted());

        assertTrue(result.stream().anyMatch(store -> store.getName().equals("Store 2")));
    }

    // -[가게 수정 테스트]----------------------------------------------------

    @DisplayName("가게 수정 성공 테스트 - 정상 수정")
    @Test
    public void testModifyStore_Success() {
        Long storeId = 1L;
        Long ownerId = 1L;

        Owner owner = mock(Owner.class);
        when(owner.getId()).thenReturn(ownerId);

        Store store = Store.builder()
                .id(storeId)
                .name("Old Store Name")
                .owner(owner)
                .description("Old Description")
                .contact("010-1111-2222")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(21, 0))
                .isDeleted(false)
                .build();

        ModifyStoreReqDto modifyStoreReqDto = new ModifyStoreReqDto();
        modifyStoreReqDto.setName("New Store Name");
        modifyStoreReqDto.setDescription("New Description");
        modifyStoreReqDto.setContact("010-9999-8888");
        modifyStoreReqDto.setMinOrderAmount(15000L);
        modifyStoreReqDto.setOpenTime(LocalTime.of(10, 0));
        modifyStoreReqDto.setEndTime(LocalTime.of(22, 0));
        modifyStoreReqDto.setIsDeleted(false);
        modifyStoreReqDto.setCategoryIds(List.of(1L, 2L));

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(storeRepository.existsByName(modifyStoreReqDto.getName())).thenReturn(false);
        doNothing().when(storeCategoryRepository).deleteByStoreId(storeId);

        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));

        ModifyStoreRespDto response = storeService.modifyStore(storeId, modifyStoreReqDto, ownerId);

        assertNotNull(response);
        assertEquals("가게 정보가 수정되었습니다..", response.getMessage());

        assertEquals("New Store Name", store.getName());
        assertEquals("New Description", store.getDescription());
        assertEquals("010-9999-8888", store.getContact());
        assertEquals(15000L, store.getMinOrderAmount());
        assertEquals(LocalTime.of(10, 0), store.getOpenTime());
        assertEquals(LocalTime.of(22, 0), store.getEndTime());
        assertFalse(store.getIsDeleted());

        verify(storeRepository).findById(storeId);
        verify(storeCategoryRepository).deleteByStoreId(storeId);
        verify(storeCategoryRepository, times(2)).save(any(StoreCategory.class));
    }

    @DisplayName("가게 수정 성공 테스트 - 가게 폐업")
    @Test
    public void testCloseStore_Success() {
        Long storeId = 1L;
        Long ownerId = 1L;

        Owner owner = mock(Owner.class);
        when(owner.getId()).thenReturn(ownerId);

        Store store = Store.builder()
                .id(storeId)
                .name("Old Store Name")
                .owner(owner)
                .description("Old Description")
                .contact("010-1111-2222")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(21, 0))
                .isDeleted(false)
                .build();

        ModifyStoreReqDto modifyStoreReqDto = new ModifyStoreReqDto();
        modifyStoreReqDto.setIsDeleted(true); // 폐업 처리

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        ModifyStoreRespDto response = storeService.modifyStore(storeId, modifyStoreReqDto, ownerId);

        assertNotNull(response);
        assertEquals("가게 정보가 수정되었습니다..", response.getMessage());

        assertEquals("Old Store Name", store.getName());
        assertEquals("Old Description", store.getDescription());
        assertEquals("010-1111-2222", store.getContact());
        assertEquals(10000L, store.getMinOrderAmount());
        assertEquals(LocalTime.of(9, 0), store.getOpenTime());
        assertEquals(LocalTime.of(21, 0), store.getEndTime());
        assertTrue(store.getIsDeleted()); // 폐업 상태 확인

        verify(storeRepository).findById(storeId);
        verify(storeCategoryRepository, times(0)).save(any(StoreCategory.class));
    }

    @DisplayName(("실패 테스트 - 존재하지 않는 카테고리 ID"))
    @Test
    public void testModifyStore_Fail_NotFoundCategory() {
        Long storeId = 1L;
        Long ownerId = 1L;

        Owner owner = mock(Owner.class);
        when(owner.getId()).thenReturn(ownerId);

        Store store = Store.builder()
                .id(storeId)
                .name("Old Store Name")
                .owner(owner)
                .description("Old Description")
                .contact("010-1111-2222")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(21, 0))
                .isDeleted(false)
                .build();

        ModifyStoreReqDto modifyStoreReqDto = new ModifyStoreReqDto();
        modifyStoreReqDto.setName("New Name");
        modifyStoreReqDto.setCategoryIds(List.of(999L)); // 존재하지 않는 카테고리 ID

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty()); // 없는 카테고리

        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.modifyStore(storeId, modifyStoreReqDto, ownerId)
        );

        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }

    @DisplayName(("실패 테스트 - 동일한 이름"))
    @Test
    public void testModifyStore_Fail_DuplicateName() {
        Long storeId = 1L;
        Long ownerId = 1L;

        Owner owner = mock(Owner.class);
        when(owner.getId()).thenReturn(ownerId);

        Store store = Store.builder()
                .id(storeId)
                .name("Old Store Name")
                .owner(owner)
                .description("Old Description")
                .contact("010-1111-2222")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(21, 0))
                .isDeleted(false)
                .build();

        ModifyStoreReqDto modifyStoreReqDto = new ModifyStoreReqDto();
        modifyStoreReqDto.setName("Duplicate Store Name");

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(storeRepository.existsByName("Duplicate Store Name")).thenReturn(true); // 이미 사용 중인 가게 이름

        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.modifyStore(storeId, modifyStoreReqDto, ownerId)
        );

        assertEquals(ErrorCode.STORENAME_ALREADY_EXISTS, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }

    @DisplayName(("실패 테스트 - 최대 생성 수 제한"))
    @Test
    public void testModifyStore_Fail_MaxStoreLimit() {
        Long storeId = 1L;
        Long ownerId = 1L;

        Owner owner = mock(Owner.class);
        when(owner.getId()).thenReturn(ownerId);

        Store store = Store.builder()
                .id(storeId)
                .name("Old Store Name")
                .owner(owner)
                .description("Old Description")
                .contact("010-1111-2222")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(21, 0))
                .isDeleted(true) // 폐업 상태
                .build();

        ModifyStoreReqDto modifyStoreReqDto = new ModifyStoreReqDto();
        modifyStoreReqDto.setIsDeleted(false);

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(storeRepository.countByOwnerIdAndIsDeletedFalse(ownerId)).thenReturn(3L); // 이미 영업 중인 가게가 3개인 경우

        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.modifyStore(storeId, modifyStoreReqDto, ownerId)
        );

        assertEquals(ErrorCode.HAVE_FULL_STORE, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }

    @DisplayName("가게 수정 실패 테스트 - owner가 다른 경우")
    @Test
    public void testModifyStore_Fail_InvalidOwner() {
        Long storeId = 1L;
        Long ownerId = 1L;
        Long differentOwnerId = 2L; // 다른 사장님 ID

        Owner storeOwner = Owner.builder().id(ownerId).build();
        Store store = Store.builder()
                .id(storeId)
                .owner(storeOwner)
                .name("Store A")
                .minOrderAmount(10000L)
                .isDeleted(false)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        ModifyStoreReqDto modifyStoreReqDto = new ModifyStoreReqDto();
        modifyStoreReqDto.setName("New Store Name");

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.modifyStore(storeId, modifyStoreReqDto, differentOwnerId)
        );

        assertEquals(ErrorCode.FORBIDDEN_ACCESS, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }

    @DisplayName("가게 수정 실패 테스트 - 수정된 내용이 없는 경우")
    @Test
    public void testModifyStore_Fail_noUpdateData() {
        Long storeId = 1L;
        Long ownerId = 1L;

        Owner storeOwner = Owner.builder().id(ownerId).build();

        Store store = Store.builder()
                .id(storeId)
                .owner(storeOwner)
                .name("Old Store Name")
                .description("Old Description")
                .contact("010-1111-2222")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)
                .build();

        ModifyStoreReqDto modifyStoreReqDto = new ModifyStoreReqDto();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                storeService.modifyStore(storeId, modifyStoreReqDto, ownerId)
        );

        assertEquals(ErrorCode.NO_UPDATE_DATA, exception.getErrorCode());
        System.out.println(exception.getMessage());
    }
    // -[카테고리 별 가게 조회]---------------------------------------------------
    @DisplayName("가게 조회 성공 테스트 - 가게 목록 반환")
    @Test
    public void testGetStoreByCategory_Success() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        Store store1 = Store.builder()
                .id(1L)
                .name("Store A")
                .minOrderAmount(10000L)
                .isDeleted(false)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        Store store2 = Store.builder()
                .id(2L)
                .name("Store B")
                .minOrderAmount(15000L)
                .isDeleted(false)
                .openTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(23, 0))
                .build();

        StoreCategory storeCategory1 = StoreCategory.builder()
                .store(store1)
                .build();

        StoreCategory storeCategory2 = StoreCategory.builder()
                .store(store2)
                .build();

        when(storeCategoryRepository.findByCategoryId(categoryId)).thenReturn(List.of(storeCategory1, storeCategory2));

        List<GetStoreRespDto> result = storeService.getStoreByCategory(categoryId);

        assertEquals(2, result.size());
        assertEquals("Store A", result.get(0).getName());
        assertEquals(10000L, result.get(0).getMinOrderAmount());
        assertEquals("Store B", result.get(1).getName());
        assertEquals(15000L, result.get(1).getMinOrderAmount());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(storeCategoryRepository, times(1)).findByCategoryId(categoryId);
    }

    @DisplayName("가게 조회 성공 테스트 - 빈 목록 반환")
    @Test
    public void testGetStoreByCategory_Fail_NotFoundStore() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        when(storeCategoryRepository.findByCategoryId(categoryId)).thenReturn(List.of());

        List<GetStoreRespDto> result = storeService.getStoreByCategory(categoryId);

        assertEquals(0, result.size());

        verify(categoryRepository, times(1)).existsById(categoryId); // 카테고리 존재 여부 확인 호출 검증
        verify(storeCategoryRepository, times(1)).findByCategoryId(categoryId);
    }

    // -[카테고리 별 가게 조회]---------------------------------------------------
    @DisplayName("가게 조회 성공 테스트 - 가게 목록 반환")
    @Test
    public void testGetStoreByCategory_Success() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        Store store1 = Store.builder()
                .id(1L)
                .name("Store A")
                .minOrderAmount(10000L)
                .isDeleted(false)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        Store store2 = Store.builder()
                .id(2L)
                .name("Store B")
                .minOrderAmount(15000L)
                .isDeleted(false)
                .openTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(23, 0))
                .build();

        StoreCategory storeCategory1 = StoreCategory.builder()
                .store(store1)
                .build();

        StoreCategory storeCategory2 = StoreCategory.builder()
                .store(store2)
                .build();

        when(storeCategoryRepository.findByCategoryId(categoryId)).thenReturn(List.of(storeCategory1, storeCategory2));

        List<GetStoreRespDto> result = storeService.getStoreByCategory(categoryId);

        assertEquals(2, result.size());
        assertEquals("Store A", result.get(0).getName());
        assertEquals(10000L, result.get(0).getMinOrderAmount());
        assertEquals("Store B", result.get(1).getName());
        assertEquals(15000L, result.get(1).getMinOrderAmount());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(storeCategoryRepository, times(1)).findByCategoryId(categoryId);
    }

    @DisplayName("가게 조회 성공 테스트 - 빈 목록 반환")
    @Test
    public void testGetStoreByCategory_Fail_NotFoundStore() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        when(storeCategoryRepository.findByCategoryId(categoryId)).thenReturn(List.of());

        List<GetStoreRespDto> result = storeService.getStoreByCategory(categoryId);

        assertEquals(0, result.size());

        verify(categoryRepository, times(1)).existsById(categoryId); // 카테고리 존재 여부 확인 호출 검증
        verify(storeCategoryRepository, times(1)).findByCategoryId(categoryId);
    }

}
