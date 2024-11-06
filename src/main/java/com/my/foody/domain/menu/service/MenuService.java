package com.my.foody.domain.menu.service;


import com.my.foody.domain.menu.dto.req.MenuUpdateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuUpdateRespDto;
import com.my.foody.domain.menu.dto.req.MenuCreateReqDto;
import com.my.foody.domain.menu.dto.resp.MenuCreateRespDto;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;


   public Menu findActiveMenuByIdOrFail(Long menuId) {
        Menu menu = menuRepository.findActivateMenu(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        if (menu.getIsSoldOut()) {
            throw new BusinessException(ErrorCode.MENU_NOT_AVAILABLE);
        }
        return menu;
    }


    // 메뉴 등록
    @Transactional
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
            menu.updateName(menuUpdateReqDto.getName());
        }

        //가격 수정
        if (menuUpdateReqDto.getPrice() != null) {
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

