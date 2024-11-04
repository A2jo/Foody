package com.my.foody.domain.user.controller;

import com.my.foody.domain.address.dto.req.AddressCreateReqDto;
import com.my.foody.domain.address.dto.req.AddressModifyReqDto;
import com.my.foody.domain.address.dto.resp.AddressCreateRespDto;
import com.my.foody.domain.address.dto.resp.AddressModifyRespDto;
import com.my.foody.domain.user.dto.req.UserLoginReqDto;
import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserInfoRespDto;
import com.my.foody.domain.user.dto.resp.UserLoginRespDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.service.UserService;
import com.my.foody.global.config.valid.CurrentUser;
import com.my.foody.global.config.valid.RequireAuth;
import com.my.foody.global.jwt.JwtVo;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import com.my.foody.global.util.api.ApiResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequireAuth(userType = UserType.USER)
    @GetMapping("/mypage")
    public ResponseEntity<ApiResult<UserInfoRespDto>> getUserInfo(@CurrentUser TokenSubject tokenSubject){
        return new ResponseEntity<>(ApiResult.success(userService.getUserInfo(tokenSubject.getId())), HttpStatus.OK);
    }

    @RequireAuth(userType = UserType.USER)
    @PostMapping("/mypage/address")
    public ResponseEntity<ApiResult<AddressCreateRespDto>> registerAddress(@RequestBody @Valid AddressCreateReqDto addressCreateReqDto,
                                                                           @CurrentUser TokenSubject tokenSubject){
        return new ResponseEntity<>(ApiResult.success(userService.registerAddress(addressCreateReqDto, tokenSubject.getId())), HttpStatus.CREATED);
    }

    @RequireAuth(userType = UserType.USER)
    @PutMapping("/mypage/address/{addressId}")
    public ResponseEntity<ApiResult<AddressModifyRespDto>> modifyAddress(@PathVariable(value = "addressId") Long addressId,
                                                                         @RequestBody @Valid AddressModifyReqDto addressModifyReqDto,
                                                                         @CurrentUser TokenSubject tokenSubject){
        return new ResponseEntity<>(ApiResult.success(userService.modifyAddress(addressModifyReqDto, tokenSubject.getId(), addressId)), HttpStatus.OK);
    }



}
