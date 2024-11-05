package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.dto.req.MenuUpdateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuUpdateRespDto;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class MenuService {

    private MenuRepository menuRepository;
    private StoreRepository storeRepository;


    @Transactional
    // 메뉴 수정
    public MenuUpdateRespDto updateMenu(Long storeId, Long menuId, MenuUpdateReqDto menuUpdateReqDto, Long ownerId) {

        //해당 가게가 존재 하는지 확인
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        //가게 주인 확인
        isStoreOwner(store, ownerId);

        //수정할 메뉴 조회
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        //메뉴명 수정
        if (menuUpdateReqDto.getName() != null) {
            if (menuUpdateReqDto.getName().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_MENU_NAME);
            }
            menu.updateName(menuUpdateReqDto.getName());
        }

        //가격 수정
        if (menuUpdateReqDto.getPrice() != null) {
            if (menuUpdateReqDto.getPrice() < 1) {
                throw new BusinessException(ErrorCode.INVALID_MENU_PRICE);
            }
            menu.updatePrice(menuUpdateReqDto.getPrice());
        }

        return new MenuUpdateRespDto();
    }


    // 가게 주인 확인 메서드
    private void isStoreOwner(Store store, Long ownerId) {
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

    }
}

