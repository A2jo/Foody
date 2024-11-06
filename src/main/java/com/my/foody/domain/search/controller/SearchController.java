package com.my.foody.domain.search.controller;

import com.my.foody.domain.search.dto.req.SearchReqDto;
import com.my.foody.domain.search.dto.resp.SearchRespDto;
import com.my.foody.domain.search.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<SearchRespDto> searchStores(@Valid SearchReqDto searchReqDto) {
        SearchRespDto searchResult = searchService.searchStores(searchReqDto);
        return ResponseEntity.ok(searchResult);
    }
}
