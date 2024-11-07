package com.my.foody.domain.search;

import com.my.foody.global.util.api.ApiResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<ApiResult<SearchRespDto>> searchStores(@RequestParam(value = "keyword", required = false)String keyword,
                                                                 @RequestParam(value = "page", defaultValue = "0")int page,
                                                                 @RequestParam(value = "limit", defaultValue = "10")int limit) {
        SearchRespDto searchResult = searchService.searchStores(keyword, page, limit);
        return new ResponseEntity<>(ApiResult.success(searchResult), HttpStatus.OK);
    }
}

