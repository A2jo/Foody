package com.my.foody.domain.store.service;


import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.dto.resp.StoreCreateRespDto;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @InjectMocks
    StoreService storeService;

    @Mock
    StoreRepository storeRepository;
    @Mock
    OwnerRepository ownerRepository;

    @Test
    public void testCreateStore_Success() {
        StoreCreateReqDto storeCreateReqDto = new StoreCreateReqDto();
        Long ownerId = 1L;
        storeCreateReqDto.setName("Test Store");
        storeCreateReqDto.setDescription("Test Description");
        storeCreateReqDto.setContact("123456789");
        storeCreateReqDto.setMinOrderAmount(1000L);
        storeCreateReqDto.setOpenTime(LocalTime.parse("11:00"));
        storeCreateReqDto.setEndTime(LocalTime.parse("21:00"));

        Owner owner = mock(Owner.class);

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(storeRepository.existsByName(storeCreateReqDto.getName())).thenReturn(false);
        when(storeRepository.countByOwnerId(ownerId)).thenReturn(2L);

        StoreCreateRespDto storeCreateRespDto = storeService.createStore(storeCreateReqDto, ownerId);

        assertNotNull(storeCreateRespDto);
        verify(storeRepository).save(any(Store.class));
    }
}
