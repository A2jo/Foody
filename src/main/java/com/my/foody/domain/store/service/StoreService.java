package com.my.foody.domain.store.service;

import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.GetStoreRespDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.storeCategory.entity.StoreCategory;
import com.my.foody.domain.storeCategory.repo.StoreCategoryRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 유효성 검사
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

    public Store findActivateStoreByIdOrFail(Long storeId) {
        return storeRepository.findActivateStore(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
    }

    public Page<GetStoreRespDto> getStoreByCategory(Long categoryId, int page, int limit) {

        validateGetStoreCategory(categoryId);

        Pageable pageable = PageRequest.of(page, limit);
        Page<StoreCategory> storeCategories = storeCategoryRepository.findByCategoryId(categoryId, pageable);
        return storeCategories.map(storeCategory -> new GetStoreRespDto(storeCategory.getStore()));
    }

    public GetStoreRespDto getStoreInfo(Long categoryId, Long storeId) {

        validateGetStoreCategory(categoryId);

        // 해당 카테고리에 해당 스토어가 있는지 확인
        StoreCategory storeCategory = storeCategoryRepository.findByCategoryIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND_IN_CATEGORY));

        // 가게 정보 DTO 변환 및 반환
        return new GetStoreRespDto(storeCategory.getStore());
    }

    // 카테고리 유효성 검사
    public void validateGetStoreCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }
}
