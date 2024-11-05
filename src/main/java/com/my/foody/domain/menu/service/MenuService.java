package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.dto.resp.MenuDeleteRespDto;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;


    @Transactional
    // 메뉴 삭제
    public MenuDeleteRespDto SoftDeleteMenu(Long storeId, Long menuId, Long ownerId) {

        //해당 가게가 존재 하는지 확인
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        //가게 주인 확인
        isStoreOwner(store, ownerId);

        //삭제 할 메뉴 조회
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        // 여기서 isDeleted = true 바꾸는 로직
        menu.softDeleteMenu();

        return new MenuDeleteRespDto();
    }


    // 가게 주인 확인 메서드
    private void isStoreOwner(Store store, Long ownerId) {
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

    }
}