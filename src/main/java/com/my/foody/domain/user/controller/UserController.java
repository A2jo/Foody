package com.my.foody.domain.user.controller;

import com.my.foody.domain.user.dto.req.UserLoginReqDto;
import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserLoginRespDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.util.api.ApiResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<ApiResult<UserSignUpRespDto>> signUp(@RequestBody @Valid UserSignUpReqDto userSignUpReqDto){
        return new ResponseEntity<>(ApiResult.success(userService.signUp(userSignUpReqDto)), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<UserLoginRespDto>> login(@RequestBody @Valid UserLoginReqDto userLoginReqDto, HttpServletResponse response){
        UserLoginRespDto loginRespDto = userService.login(userLoginReqDto);
        response.setHeader(JwtVo.HEADER, loginRespDto.getToken());
        return new ResponseEntity<>(ApiResult.success(loginRespDto), HttpStatus.OK);
    }



}
