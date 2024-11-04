package com.my.foody.domain.owner.controller;


import com.my.foody.domain.owner.dto.req.OwnerJoinReqDto;
import com.my.foody.domain.owner.dto.req.OwnerLoginReqDto;
import com.my.foody.domain.owner.dto.resp.OwnerJoinRespDto;
import com.my.foody.domain.owner.dto.resp.OwnerLoginRespDto;
import com.my.foody.domain.owner.dto.resp.OwnerMyPageRespDto;
import com.my.foody.domain.owner.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<OwnerJoinRespDto> signup(@RequestBody OwnerJoinReqDto ownerJoinReqDto) {
        OwnerJoinRespDto responseMessage = ownerService.signup(ownerJoinReqDto);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<OwnerLoginRespDto> login(@RequestBody OwnerLoginReqDto ownerLoginReqDto) {
        OwnerLoginRespDto responseMessage = ownerService.login(ownerLoginReqDto);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @GetMapping("/mypage")
    public ResponseEntity<OwnerMyPageRespDto> getMyPage(@AuthenticationPrincipal Long ownerId) {
        OwnerMyPageRespDto response = ownerService.getMyPage(ownerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
