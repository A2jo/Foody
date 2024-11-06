package com.my.foody.domain.store.service;

import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;
import com.my.foody.domain.menu.dto.resp.GetMenuRespDto;
import com.my.foody.domain.menu.dto.resp.MenuListRespDto;
import com.my.foody.domain.menu.dto.resp.MenuListRespDto.PageInfo;
import com.my.foody.domain.menu.repo.MenuProjection;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.review.repo.ReviewRepository;
import com.my.foody.domain.store.dto.req.ModifyStoreReqDto;
import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.GetStoreRespDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.dto.resp.StoreListRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.storeCategory.entity.StoreCategory;
import com.my.foody.domain.storeCategory.repo.StoreCategoryProjection;
import com.my.foody.domain.storeCategory.repo.StoreCategoryRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final OwnerRepository ownerRepository;
    private final CategoryRepository categoryRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;

    // 사장님 가게 생성
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

    // 사장님 가게 목록 조회
    public List<GetStoreRespDto> getAllStoresByOwnerId(Long ownerId) {
        // 해당 ID의 가게 조회
        List<Store> storeList = storeRepository.findByOwnerId(ownerId);

        return storeList.stream()
                .map(store -> new GetStoreRespDto(store))
                .toList();
    }

    // 사장님 가게 수정
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

        store.updateAll(modifyStoreReqDto);

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
        // 수정할 데이터가 없으면 예외 발생
        if (modifyStoreReqDto.hasNoUpdateData()) {
            throw new BusinessException(ErrorCode.NO_UPDATE_DATA);
        }
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

    public Store findActivateStoreByIdOrFail(Long storeId) {
        return storeRepository.findActivateStore(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
    }

    // 카테고리별 가게목록 조회
    public StoreListRespDto getStoreByCategory(Long categoryId, int page, int limit) {
        // 유효성 검사 - 카테고리를 찾을 수 없는 경우
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, limit);
        Page<StoreCategoryProjection> storeProjections = storeCategoryRepository.findStoresByCategoryId(categoryId, pageable);

        Page<GetStoreRespDto> storeDtos = storeProjections.map(projection ->
                new GetStoreRespDto(projection.getStoreId(), projection.getStoreName(), projection.getMinOrderAmount()));

        return new StoreListRespDto(storeDtos);
    }

    // 가게 상세목록 조회
    public GetStoreRespDto getStoreInfo(Long categoryId, Long storeId) {

        StoreCategory storeCategory = validateGetStoreInfo(categoryId, storeId);

        // 리뷰 개수 조회
        long reviewCount = reviewRepository.countByStoreId(storeId);

        // 가게 정보 DTO 변환 및 반환
        return new GetStoreRespDto(storeCategory.getStore(), reviewCount);
    }

    // 가게 상세보기 유효성 검사
    public StoreCategory validateGetStoreInfo(Long categoryId, Long storeId) {
        // 카테고리가 존재하는지 확인
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        // 해당 카테고리에 해당 스토어가 있는지 확인
        StoreCategory storeCategory = storeCategoryRepository.findByCategoryIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND_IN_CATEGORY));
        // 해강 가게가 영업중인지 확인
        if (storeCategory.getStore().getIsDeleted()) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        return storeCategory;
    }

    public MenuListRespDto getStoreMenus(Long storeId, Long categoryId, int page, int limit) {
        // 유효성 검사
        validateGetStoreMenu(storeId, categoryId);

        Pageable pageable = PageRequest.of(page, limit);
        Page<MenuProjection> menuProjections = menuRepository.findMenusByStoreId(storeId, pageable);

        // 유효성 검사 - 메뉴가 존재하는지 확인
        validateMenuExists(menuProjections);

        Page<GetMenuRespDto> getMenuRespDtos = menuProjections.map(projection ->
                new GetMenuRespDto(projection.getId(), projection.getName(), projection.getPrice())
        );

        return new MenuListRespDto(getMenuRespDtos);
    }
    // 가게 메뉴 조회 유효성 검사
    public void validateGetStoreMenu(Long storeId, Long categoryId) {
        // 카테고리가 있는지
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        // 선택한 카테고리에 선택한 가게가 있는지
        storeCategoryRepository.findByCategoryIdAndStoreId(categoryId, storeId).orElseThrow(() ->
                new BusinessException(ErrorCode.STORE_NOT_FOUND_IN_CATEGORY)
        );
        // 가게 상태가 폐업인지
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        if (store.getIsDeleted()) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
    }
    // 메뉴가 존재하는지 유효성 검사
    private void validateMenuExists(Page<MenuProjection> menuProjections) {
        if (menuProjections.isEmpty()) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND_IN_STORE);
        }
    }

    // 카테고리별 가게목록 조회
    public StoreListRespDto getStoreByCategory(Long categoryId, int page, int limit) {
        // 유효성 검사 - 카테고리를 찾을 수 없는 경우
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, limit);
        Page<StoreCategoryProjection> storeProjections = storeCategoryRepository.findStoresByCategoryId(categoryId, pageable);

        Page<GetStoreRespDto> storeDtos = storeProjections.map(projection ->
                new GetStoreRespDto(projection.getStoreId(), projection.getStoreName(), projection.getMinOrderAmount()));

        return new StoreListRespDto(storeDtos);
    }

    public List<GetStoreRespDto> getStoreByCategory(Long categoryId) {
        List<StoreCategory> storeCategories = storeCategoryRepository.findByCategoryId(categoryId);
        // 유효성 검사 - 카테고리를 찾을 수 없는 경우
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return storeCategories.stream()
                .map(storeCategory -> new GetStoreRespDto(storeCategory.getStore()))
                .toList();
    }

        Pageable pageable = PageRequest.of(page, limit);
        Page<StoreCategory> storeCategories = storeCategoryRepository.findByCategoryId(categoryId, pageable);
        return storeCategories.map(storeCategory -> new GetStoreRespDto(storeCategory.getStore()));
        public StoreListRespDto getStoreByCategory (Long categoryId,int page, int limit){
            // 유효성 검사 - 카테고리를 찾을 수 없는 경우
            if (!categoryRepository.existsById(categoryId)) {
                throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
            }

            Pageable pageable = PageRequest.of(page, limit);
            Page<StoreCategoryProjection> storeProjections = storeCategoryRepository.findStoresByCategoryId(categoryId, pageable);

            Page<GetStoreRespDto> storeDtos = storeProjections.map(projection ->
                    new GetStoreRespDto(projection.getStoreId(), projection.getStoreName(), projection.getMinOrderAmount()));

            return new StoreListRespDto(storeDtos);
        }

    // 가게 상세목록 조회
        public List<GetStoreRespDto> getStoreByCategory (Long categoryId){
            List<StoreCategory> storeCategories = storeCategoryRepository.findByCategoryId(categoryId);
            // 유효성 검사 - 카테고리를 찾을 수 없는 경우
            if (!categoryRepository.existsById(categoryId)) {
                throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            return storeCategories.stream()
                    .map(storeCategory -> new GetStoreRespDto(storeCategory.getStore()))
                    .toList();
        }
    }

    public GetStoreRespDto getStoreInfo(Long categoryId, Long storeId) {

        StoreCategory storeCategory = validateGetStoreInfo(categoryId, storeId);

        // 리뷰 개수 조회
        long reviewCount = reviewRepository.countByStoreId(storeId);

        // 가게 정보 DTO 변환 및 반환
        return new GetStoreRespDto(storeCategory.getStore(), reviewCount);
    }

    // 가게 상세보기 유효성 검사
    public StoreCategory validateGetStoreInfo(Long categoryId, Long storeId) {
        // 카테고리가 존재하는지 확인
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        // 해당 카테고리에 해당 스토어가 있는지 확인
        StoreCategory storeCategory = storeCategoryRepository.findByCategoryIdAndStoreId(categoryId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND_IN_CATEGORY));
        // 해강 가게가 영업중인지 확인
        if (storeCategory.getStore().getIsDeleted()) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        return storeCategory;
    }
}
