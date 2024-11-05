package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.dto.req.MenuCreateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuCreateRespDto;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MenuService {

    private MenuRepository menuRepository;
    private StoreRepository storeRepository;

    // 메뉴 등록
    public MenuCreateRespDto createMenu(Long storeId, MenuCreateReqDto menuCreateReqDto, Long ownerId) {


        Store store = storeRepository.findById(storeId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        //가게 주인 확인
        isStoreOwner(store, ownerId);

        Menu menu = Menu.builder()
                .store(store)
                .name(menuCreateReqDto.getName())
                .price(menuCreateReqDto.getPrice())
                .isSoldOut(false)
                .isDeleted(false)
                .build();

        menuRepository.save(menu);

        // 메세제 응답 반환
        return new MenuCreateRespDto();
    }

    // 가게 주인 확인 메서드
    private void isStoreOwner(Store store, Long ownerId) {
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

    }
}
