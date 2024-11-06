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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final OwnerRepository ownerRepository;
    private final CategoryRepository categoryRepository;
    private final StoreCategoryRepository storeCategoryRepository;

    @Transactional
    public StoreCreateRespDto createStore(StoreCreateReqDto storeCreateReqDto, Long ownerId) {
        // owner 조회
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(() ->
                new BusinessException(ErrorCode.OWNER_NOT_FOUND)
        );
        // 유효성 검사
        validateCreateStore(storeCreateReqDto, ownerId);
        // 가게 생성
        Store store = storeCreateReqDto.toEntity(owner);
        storeRepository.save(store);
        // 카테고리 저장
        saveCategory(storeCreateReqDto, store);

        return new StoreCreateRespDto(store);
    }

    public List<GetStoreRespDto> getAllStoresByOwnerId(Long ownerId) {
        // 해당 ID의 가게 조회
        List<Store> storeList = storeRepository.findByOwnerId(ownerId);

        return storeList.stream()
                .map(store -> new GetStoreRespDto(store))
                .toList();
    }

    @Transactional
    public ModifyStoreRespDto modifyStore(Long storeId, ModifyStoreReqDto modifyStoreReqDto, Long ownerId) {
        // 가게 조회
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new BusinessException(ErrorCode.STORE_NOT_FOUND)
        );
        // 접속한 owner가 가게의 owner가 맞는지 확인
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        //유효성 검사
        validateModifyStore(store, modifyStoreReqDto, ownerId);

        // 수정 flag
        boolean isModified = false;

        // 수정할 필드가 null이 아닌 경우만 업데이트
        if (modifyStoreReqDto.getName() != null) {
            store.setName(modifyStoreReqDto.getName());
            isModified = true;
        }
        if (modifyStoreReqDto.getDescription() != null) {
            store.setDescription(modifyStoreReqDto.getDescription());
            isModified = true;
        }
        if (modifyStoreReqDto.getContact() != null) {
            store.setContact(modifyStoreReqDto.getContact());
            isModified = true;
        }
        if (modifyStoreReqDto.getMinOrderAmount() != null) {
            store.setMinOrderAmount(modifyStoreReqDto.getMinOrderAmount());
            isModified = true;
        }
        if (modifyStoreReqDto.getOpenTime() != null) {
            store.setOpenTime(modifyStoreReqDto.getOpenTime());
            isModified = true;
        }
        if (modifyStoreReqDto.getEndTime() != null) {
            store.setEndTime(modifyStoreReqDto.getEndTime());
            isModified = true;
        }
        if (modifyStoreReqDto.getIsDeleted() != null) {
            store.setIsDeleted(modifyStoreReqDto.getIsDeleted());
            isModified = true;
        }

        // 아무것도 수정되지 않은 경우 예외 처리
        if (!isModified) {
            throw new BusinessException(ErrorCode.NO_UPDATE_DATA);
        }

        // 가게가 폐업 상태가 아닌 경우에만 카테고리 수정
        if (modifyStoreReqDto.getIsDeleted() == null || !modifyStoreReqDto.getIsDeleted()) {
            if (modifyStoreReqDto.getCategoryIds() != null) {
                storeCategoryRepository.deleteByStoreId(storeId);
                if (!modifyStoreReqDto.getCategoryIds().isEmpty()) {
                    modifyCategory(modifyStoreReqDto, store);
                }
            }
        }
        return new ModifyStoreRespDto(store);
    }

    // 카테고리 저장
    public void saveCategory(StoreCreateReqDto storeCreateReqDto, Store store) {
        List<Long> categoryIds = storeCreateReqDto.getCategoryIds();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                    new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
            );
            StoreCategory storeCategory = StoreCategory.builder()
                    .store(store)
                    .category(category)
                    .build();
            storeCategoryRepository.save(storeCategory);
        }
    }
    // 카테고리 수정
    public void modifyCategory(ModifyStoreReqDto modifyStoreReqDto, Store store) {
        List<Long> categoryIds = modifyStoreReqDto.getCategoryIds();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                    new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
            );
            StoreCategory storeCategory = StoreCategory.builder()
                    .store(store)
                    .category(category)
                    .build();
            storeCategoryRepository.save(storeCategory);
        }
    }

    // 유효성 검사
    // 가게 생성 시 유효성 검사
    public void validateCreateStore(StoreCreateReqDto storeCreatereqDto, Long ownerId) {
        // 가게이름 중복 검사
        if (storeRepository.existsByName(storeCreatereqDto.getName())) {
            throw new BusinessException(ErrorCode.STORENAME_ALREADY_EXISTS);
        }
        // 사장님 영업중인 가게 3개 이상인 경우 생성 불가
        if (storeRepository.countByOwnerIdAndIsDeletedFalse(ownerId) >= 3) {
            throw new BusinessException(ErrorCode.HAVE_FULL_STORE);
        }
    }
    // 가게 수정 시 유효성 검사
    public void validateModifyStore(Store store, ModifyStoreReqDto modifyStoreReqDto, Long ownerId) {
        // 요청한 사용자가 가게의 실제 사장님인지 확인
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        // 가게 이름 중복 검사 (name이 null이 아닌 경우에만 검사)
        if (modifyStoreReqDto.getName() != null && storeRepository.existsByName(modifyStoreReqDto.getName())) {
            throw new BusinessException(ErrorCode.STORENAME_ALREADY_EXISTS);
        }
        // 사장님 영업중인 가게 3개 이상인 경우 생성 불가
        if (storeRepository.countByOwnerIdAndIsDeletedFalse(ownerId) >= 3) {
            throw new BusinessException(ErrorCode.HAVE_FULL_STORE);
        }
    }

    public Store findActivateStoreByIdOrFail(Long storeId){
        return storeRepository.findActivateStore(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
    }
}
