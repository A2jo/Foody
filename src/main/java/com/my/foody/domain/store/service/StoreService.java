package com.my.foody.domain.store.service;

import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.UserType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

//    @RequireAuth(userType = UserType.OWNER)
    public StoreCreateRespDto createStore(@Valid StoreCreateReqDto storeCreatereqDto) {
        // 가게이름 중복 검사
        if(storeRepository.existsByName(storeCreatereqDto.getName())){
            throw new BusinessException(ErrorCode.STORENAME_ALREADY_EXISTS);
        }
        //

    }
}
