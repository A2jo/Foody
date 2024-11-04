package com.my.foody.domain.store.service;

import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
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
    private final OwnerRepository ownerRepository;

    @RequireAuth(userType = UserType.OWNER)
    public StoreCreateRespDto createStore(@Valid StoreCreateReqDto storeCreatereqDto, Long ownerId) {
        // owner 조회
        Owner owner = ownerRepository.findById(ownerId).orElseThrow(() ->
                new BusinessException(ErrorCode.OWNER_NOT_FOUND)
        );
        // 가게이름 중복 검사
        if(storeRepository.existsByName(storeCreatereqDto.getName())){
            throw new BusinessException(ErrorCode.STORENAME_ALREADY_EXISTS);
        }
        // 사장님 가게 3개 이상 생성 불가
        if(storeRepository.countByOwnerId(ownerId) >= 3){
            throw new BusinessException(ErrorCode.HAVE_FULL_STORE);
        }
        // 가게 생성
        Store store = Store.builder().name(storeCreatereqDto.getName())
                .owner(owner)
                .description(storeCreatereqDto.getDescription())
                .contact(storeCreatereqDto.getContact())
                .minOrderAmount(storeCreatereqDto.getMinOrderAmount())
                .openTime(storeCreatereqDto.getOpenTime())
                .endTime(storeCreatereqDto.getEndTime())
                .build();

        storeRepository.save(store);

        return new StoreCreateRespDto(store);
    }
}
