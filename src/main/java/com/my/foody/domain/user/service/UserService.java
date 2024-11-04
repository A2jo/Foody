package com.my.foody.domain.user.service;

import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserSignUpRespDto signUp(UserSignUpReqDto userSignUpReqDto){
        //이메일 중복 검사
        if(userRepository.existsByEmail(userSignUpReqDto.getEmail())){
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        //닉네임 중복 검사
        if(userRepository.existsByNickname(userSignUpReqDto.getNickname())){
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        User user = userSignUpReqDto.toEntity();
        userRepository.save(user);
        return new UserSignUpRespDto();
    }

}
