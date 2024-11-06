package com.my.foody.domain.home.controller;

import com.my.foody.domain.home.dto.resp.MainHomeRespDto;
import com.my.foody.domain.home.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public MainHomeRespDto getMainHome() {
        return homeService.getMainHome();
    }
}
