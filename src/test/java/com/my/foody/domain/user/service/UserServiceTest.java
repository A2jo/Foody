package com.my.foody.domain.user.service;

import com.my.foody.domain.address.dto.req.AddressCreateReqDto;
import com.my.foody.domain.address.dto.req.AddressModifyReqDto;
import com.my.foody.domain.address.dto.resp.AddressCreateRespDto;
import com.my.foody.domain.address.dto.resp.AddressModifyRespDto;
import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.address.service.AddressService;
import com.my.foody.domain.user.dto.req.UserLoginReqDto;
import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserInfoRespDto;
import com.my.foody.domain.user.dto.resp.UserLoginRespDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.TokenSubject;
import com.my.foody.global.util.DummyObject;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AddressService addressService;


    @InjectMocks
    private UserService userService;

    @Mock
    private AddressRepository addressRepository;

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

    @Test
    @DisplayName("마이페이지 조회 성공 테스트")
    void getUserInfo_Success(){
        User user = mockUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //when
        UserInfoRespDto result = userService.getUserInfo(user.getId());

        //then
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getContact()).isEqualTo(user.getContact());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("마이페이지 조회 실패 테스트: 존재하지 않는 유저")
    void getUserInfo_UserNotFound(){
        User user = mockUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.getUserInfo(user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("주소지 등록 성공 테스트")
    void registerAddress_Success(){
        //given
        Long userId=1L;
        User user = mockUser();
        Address address = mockAddress(user);
        AddressCreateReqDto addressCreateReqDto = AddressCreateReqDto.builder()
                .roadAddress(address.getRoadAddress())
                .detailedAddress(address.getDetailedAddress())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        //when
        AddressCreateRespDto result = userService.registerAddress(addressCreateReqDto, userId);

        //then
        verify(userRepository, times(1)).findById(userId);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("주소지 등록 실패 테스트: 존재하지 않는 사용자")
    void registerAddress_UserNotFound() {
        // given
        Long userId = 9L;
        User user = mockUser();
        Address address = mockAddress(user);
        AddressCreateReqDto addressCreateReqDto = AddressCreateReqDto.builder()
                .roadAddress(address.getRoadAddress())
                .detailedAddress(address.getDetailedAddress())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () ->
                userService.registerAddress(addressCreateReqDto, userId)
        );
        verify(userRepository, times(1)).findById(userId);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    @DisplayName("주소지 수정 성공 테스트")
    void modifyAddress_Success(){
        Long userId = 1L;
        Long addressId = 1L;
        User user = newUser(userId);
        Address address = mockAddress(user);

        AddressModifyReqDto modifyReqDto = AddressModifyReqDto.builder()
                .roadAddress("새로운 도로명주소")
                .detailedAddress("새로운 상세주소")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressService.findByIdOrFail(addressId)).thenReturn(address);

        // when
        AddressModifyRespDto result = userService.modifyAddress(modifyReqDto, userId, addressId);

        // then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(addressService).findByIdOrFail(addressId);
    }

    @Test
    @DisplayName("주소지 수정 실패 테스트: 존재하지 않는 사용자")
    void modifyAddress_UserNotFound() {
        Long userId = 1L;
        Long addressId = 1L;
        User user = newUser(userId);

        AddressModifyReqDto modifyReqDto = AddressModifyReqDto.builder()
                .roadAddress("새로운 도로명주소")
                .detailedAddress("새로운 상세주소")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.modifyAddress(modifyReqDto, userId, addressId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        verify(userRepository).findById(userId);
        verify(addressService, never()).findByIdOrFail(anyLong());
    }

    @Test
    @DisplayName("주소지 수정 실패 테스트: 존재하지 않는 주소지")
    void modifyAddress_AddressNotFound() {
        Long userId = 1L;
        Long addressId = 1L;
        User user = newUser(userId);

        AddressModifyReqDto modifyReqDto = AddressModifyReqDto.builder()
                .roadAddress("새로운 도로명주소")
                .detailedAddress("새로운 상세주소")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressService.findByIdOrFail(addressId))
                .thenThrow(new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        assertThatThrownBy(() -> userService.modifyAddress(modifyReqDto, userId, addressId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADDRESS_NOT_FOUND);
        verify(userRepository).findById(userId);
        verify(addressService).findByIdOrFail(addressId);
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