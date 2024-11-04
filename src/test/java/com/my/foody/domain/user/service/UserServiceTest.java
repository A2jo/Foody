package com.my.foody.domain.user.service;

import com.my.foody.domain.user.dto.req.UserLoginReqDto;
import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserLoginRespDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.util.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserService userService;

    @DisplayName(value = "회원가입 성공 테스트")
    @Test
    void signup_Success() throws Exception{
        //given
        UserSignUpReqDto signUpReqDto = mockUserSignUpReqDto();

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

    @Test
    @DisplayName(value = "회원가입 실패 테스트: 이메일 중복")
    void signup_EmailAlreadyExists(){
        //given
        UserSignUpReqDto signUpReqDto = mockUserSignUpReqDto();

        when(userRepository.existsByEmail(signUpReqDto.getEmail())).thenReturn(true);

        //then
        assertThatThrownBy(() -> userService.signUp(signUpReqDto))
                          .isInstanceOf(BusinessException.class)
                          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);
        verify(userRepository, times(1)).existsByEmail(signUpReqDto.getEmail());
        verify(userRepository, never()).existsByNickname(signUpReqDto.getNickname());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName(value = "회원가입 실패 테스트: 닉네임 중복")
    void signup_NicknameAlreadyExists(){
        //given
        UserSignUpReqDto signUpReqDto = mockUserSignUpReqDto();

        when(userRepository.existsByEmail(signUpReqDto.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(signUpReqDto.getNickname())).thenReturn(true);

        //then
        assertThatThrownBy(() -> userService.signUp(signUpReqDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXISTS);
        verify(userRepository, times(1)).existsByEmail(signUpReqDto.getEmail());
        verify(userRepository, times(1)).existsByNickname(signUpReqDto.getNickname());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName(value = "로그인 성공 테스트")
    void login_Success(){
        //given
        String email = "user1234@naver.com";
        String password = "Maeda1234!";
        String mockToken = "mockToken";
        User user = User.builder()
                .email(email)
                .password(PasswordEncoder.encode(password))
                .build();
        UserLoginReqDto userLoginReqDto = mockUserLoginReqDto(email, password);

        when(userRepository.findByEmail(userLoginReqDto.getEmail())).thenReturn(Optional.of(user));
        when(jwtProvider.create(any(TokenSubject.class))).thenReturn(mockToken);

        //when
        UserLoginRespDto result = userService.login(userLoginReqDto);

        //then
        assertThat(result.getToken()).isEqualTo(mockToken);
        verify(userRepository, times(1)).findByEmail(userLoginReqDto.getEmail());
        verify(jwtProvider, times(1)).create(any(TokenSubject.class));
    }

    @Test
    @DisplayName(value = "로그인 실패 테스트: 존재하지 않는 이메일")
    void login_UserNotFound(){
        //given
        String email = "user1234@naver.com";
        String password = "Maeda1234!";
        UserLoginReqDto userLoginReqDto = mockUserLoginReqDto(email, password);

        when(userRepository.findByEmail(userLoginReqDto.getEmail())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.login(userLoginReqDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        verify(userRepository, times(1)).findByEmail(userLoginReqDto.getEmail());
        verify(jwtProvider, never()).create(any(TokenSubject.class));
    }

    @Test
    @DisplayName(value = "로그인 실패 테스트: 비밀번호 불일치")
    void login_InvalidPassword(){
        //given
        String email = "user1234@naver.com";
        String password = "Maeda1234!";
        UserLoginReqDto userLoginReqDto = mockUserLoginReqDto(email, password);
        User user = User.builder()
                .email(email)
                .password(PasswordEncoder.encode(password+"000"))
                .build();

        when(userRepository.findByEmail(userLoginReqDto.getEmail())).thenReturn(Optional.of(user));

        //when & then
        assertThatThrownBy(() -> userService.login(userLoginReqDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
        verify(userRepository, times(1)).findByEmail(userLoginReqDto.getEmail());
        verify(jwtProvider, never()).create(any(TokenSubject.class));
    }


    private UserSignUpReqDto mockUserSignUpReqDto(){
        return UserSignUpReqDto.builder()
                .contact("010-1234-5678")
                .email("user1234@naver.com")
                .password(PasswordEncoder.encode("Password1234!"))
                .name("userA")
                .nickname("userrrr")
                .build();
    }
    private UserLoginReqDto mockUserLoginReqDto(String email, String password){
        return UserLoginReqDto.builder()
                .email(email)
                .password(password)
                .build();
    }

}