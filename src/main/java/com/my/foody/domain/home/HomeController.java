package com.my.foody.domain.home;

import com.my.foody.global.util.api.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResult<MainHomeRespDto>> getMainHome() {
        return new ResponseEntity<>(ApiResult.success(homeService.getMainHome()), HttpStatus.OK);
    }
}
