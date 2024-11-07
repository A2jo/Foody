package com.my.foody.domain.search;

import com.my.foody.domain.store.repo.StoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final StoreRepository storeRepository;

    public SearchService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public SearchRespDto searchStores(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);

        List<SearchRespDto.StoreDto> stores = storeRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(keyword, pageable)
                .stream()
                .map(store -> new SearchRespDto.StoreDto(store.getId(), store.getName(), store.getMinOrderAmount()))
                .collect(Collectors.toList());

        return new SearchRespDto(stores);
    }
}

