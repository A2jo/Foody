package com.my.foody.domain.menu.service;

import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
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

    public Menu findActiveMenuByIdOrFail(Long menuId){
        Menu menu = menuRepository.findActivateMenu(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        if(menu.getIsSoldOut()){
            throw new BusinessException(ErrorCode.MENU_NOT_AVAILABLE);
        }
        return menu;
    }
}
