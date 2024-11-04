package com.my.foody.domain.user.service;

import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserSignUpRespDto join(UserSignUpReqDto userSignUpReqDto){
        //이메일 중복 검사

        //닉네임 중복 검사

        //엔티티 전환(비밀번호 인코딩)

    }

}
