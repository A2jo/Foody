package com.my.foody.domain.search.service;

import com.my.foody.domain.search.dto.req.SearchReqDto;
import com.my.foody.domain.search.dto.resp.SearchRespDto;
import com.my.foody.domain.store.repo.StoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final StoreRepository storeRepository;

    public SearchService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public SearchRespDto searchStores(SearchReqDto searchReqDto) {
        PageRequest pageRequest = PageRequest.of(searchReqDto.getPage(), searchReqDto.getLimit());

        List<SearchRespDto.StoreDto> stores = storeRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(searchReqDto.getKeyword(), pageRequest)
                .stream()
                .map(store -> new SearchRespDto.StoreDto(store.getId(), store.getName(), store.getMinOrderAmount()))
                .collect(Collectors.toList());

        return new SearchRespDto(stores);
    }
}
