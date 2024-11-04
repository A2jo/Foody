package com.my.foody.domain.owner.service;

import com.my.foody.domain.owner.dto.req.OwnerJoinReqDto;
import com.my.foody.domain.owner.dto.resp.OwnerJoinRespDto;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SUCCESS_MESSAGE = "회원가입 완료 되었습니다.";


    @Autowired
    public OwnerService(OwnerRepository ownerRepository, PasswordEncoder passwordEncoder) {
        this.ownerRepository = ownerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public OwnerJoinRespDto signup(OwnerJoinReqDto reqDto) {
        validateEmailUniqueness(reqDto.getEmail());

        Owner owner = Owner.builder()
                .name(reqDto.getName())
                .contact(reqDto.getContact())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .isDeleted(false)
                .build();

        ownerRepository.save(owner);

        return new OwnerJoinRespDto(SUCCESS_MESSAGE);
    }

    // 이메일 중복 확인
    private void validateEmailUniqueness(String email) {
        if (ownerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }
}
