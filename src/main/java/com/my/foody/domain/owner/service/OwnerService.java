package com.my.foody.domain.owner.service;

import com.my.foody.domain.owner.dto.req.OwnerJoinReqDto;
import com.my.foody.domain.owner.dto.req.OwnerLoginReqDto;
import com.my.foody.domain.owner.dto.resp.OwnerJoinRespDto;
import com.my.foody.domain.owner.dto.resp.OwnerLoginRespDto;
import com.my.foody.domain.owner.dto.resp.OwnerMyPageRespDto;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.jwt.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import com.my.foody.global.util.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final JwtProvider jwtProvider;


    private static final String LOGIN_SUCCESS_MESSAGE = "로그인 성공";

    @Autowired
    public OwnerService(OwnerRepository ownerRepository, JwtProvider jwtProvider) {
        this.ownerRepository = ownerRepository;
        this.jwtProvider = jwtProvider;
    }

    public OwnerJoinRespDto signup(OwnerJoinReqDto reqDto) {
        validateEmailUniqueness(reqDto.getEmail());

        Owner owner = Owner.builder()
                .name(reqDto.getName())
                .contact(reqDto.getContact())
                .email(reqDto.getEmail())
                .password(PasswordEncoder.encode(reqDto.getPassword()))
                .isDeleted(false)
                .build();

        ownerRepository.save(owner);

        return new OwnerJoinRespDto();
    }

    // 로그인 메서드 추가
    public OwnerLoginRespDto login(OwnerLoginReqDto reqDto) {
        // 1. 이메일로 Owner 조회
        Owner owner = ownerRepository.findByEmail(reqDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 검증
        if (!PasswordEncoder.matches(reqDto.getPassword(), owner.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        // 3. JWT 토큰 생성
        String token = jwtProvider.create(new TokenSubject(owner.getId(), UserType.OWNER));

        // 4. 로그인 성공 응답 생성
        return new OwnerLoginRespDto(token);
    }

    //마이페이지 조회
    public OwnerMyPageRespDto getMyPage(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("사장님 정보를 찾을 수 없습니다."));
        return new OwnerMyPageRespDto(owner.getId(), owner.getName(), owner.getContact(), owner.getEmail());
    }

    // 이메일 중복 확인
    private void validateEmailUniqueness(String email) {
        if (ownerRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
}