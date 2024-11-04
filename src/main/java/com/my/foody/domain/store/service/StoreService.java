package com.my.foody.domain.store.service;

import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
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
    public StoreCreateRespDto createStore(@Valid StoreCreateReqDto storeCreatereqDto, Long ownerId) {
        // 가게이름 중복 검사
        if(storeRepository.existsByName(storeCreatereqDto.getName())){
            throw new BusinessException(ErrorCode.STORENAME_ALREADY_EXISTS);
        }
        // 사장님 가게 3개 이상 생성 불가
        if(storeRepository.countByOwnerId(ownerId) >= 3){
            throw new BusinessException(ErrorCode.HAVE_FULL_STORE);
        }

    }
}
