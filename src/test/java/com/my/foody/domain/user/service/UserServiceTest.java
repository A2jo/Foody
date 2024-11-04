package com.my.foody.domain.user.service;

import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.global.util.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @DisplayName(value = "회원가입 성공 테스트")
    @Test
    void 회원가입_성공_테스트() throws Exception{
        //given
        UserSignUpReqDto signUpReqDto = UserSignUpReqDto.builder()
                .contact("010-1234-5678")
                .email("user1234@naver.com")
                .password(PasswordEncoder.encode("Password1234!"))
                .name("userA")
                .nickname("userrrr")
                .build();

        when(userRepository.existsByEmail(signUpReqDto.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(signUpReqDto.getNickname())).thenReturn(false);

        //when
        UserSignUpRespDto result = userService.signUp(signUpReqDto);

        //then
        assertThat(result.getMessage()).isEqualTo("회원가입 되었습니다");
        verify(userRepository, times(1)).existsByEmail(signUpReqDto.getEmail());
        verify(userRepository, times(1)).existsByNickname(signUpReqDto.getNickname());
        verify(userRepository, times(1)).save(any(User.class));
    }

}