package com.my.foody.domain.store.controller;

import com.my.foody.domain.store.dto.req.StoreCreateReqDto;
import com.my.foody.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/stores")
    public ResponseEntity<String> createStore(@RequestBody @Valid StoreCreateReqDto reqDto) {
        storeService.createStore(reqDto);
        return ResponseEntity.ok("가게 생성 성공");
    }

}
