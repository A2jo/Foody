package com.my.foody.domain.owner;

import com.my.foody.domain.owner.dto.req.OwnerDeleteReqDto;
import com.my.foody.domain.owner.dto.req.OwnerJoinReqDto;
import com.my.foody.domain.owner.dto.req.OwnerLoginReqDto;
import com.my.foody.domain.owner.dto.req.OwnerMyPageUpdateReqDto;
import com.my.foody.domain.owner.dto.resp.*;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.owner.service.OwnerService;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.util.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private OwnerService ownerService;

    private Owner owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = Owner.builder()
                .name("KIM HYEJIN")
                .email("kim@example.com")
                .password(PasswordEncoder.encode("Password123"))
                .contact("01012345678")
                .isDeleted(false)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 케이스 - KIM HYEJIN")
    void signupSuccess() {
        // given
        OwnerJoinReqDto reqDto = new OwnerJoinReqDto("KIM HYEJIN", "kim@example.com", "Password123", "01012345678", false);

        // when
        when(ownerRepository.existsByEmail(any())).thenReturn(false);
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
        OwnerJoinRespDto response = ownerService.signup(reqDto);

        // then
        assertThat(response).isNotNull();
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    @DisplayName("회원가입 실패 케이스 - 이메일 중복 - KIM HYEJIN")
    void signupFailDuplicateEmail() {
        // given
        OwnerJoinReqDto reqDto = new OwnerJoinReqDto("KIM HYEJIN", "kim@example.com", "Password123", "01012345678", false);

        // when
        when(ownerRepository.existsByEmail(any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> ownerService.signup(reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.EMAIL_ALREADY_EXISTS.getMsg());
    }

    @Test
    @DisplayName("로그인 성공 케이스 - KIM HYEJIN")
    void loginSuccess() {
        // given
        OwnerLoginReqDto reqDto = new OwnerLoginReqDto("kim@example.com", "Password123");

        // when
        when(ownerRepository.findByEmail(any())).thenReturn(Optional.of(owner));
        when(jwtProvider.create(any())).thenReturn("sample-token");
        OwnerLoginRespDto response = ownerService.login(reqDto);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("sample-token");
    }

    @Test
    @DisplayName("로그인 실패 케이스 - 비밀번호 불일치 - KIM HYEJIN")
    void loginFailInvalidPassword() {
        // given
        OwnerLoginReqDto reqDto = new OwnerLoginReqDto("kim@example.com", "WrongPassword123");

        // when
        when(ownerRepository.findByEmail(any())).thenReturn(Optional.of(owner));

        // then
        assertThatThrownBy(() -> ownerService.login(reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_PASSWORD.getMsg());
    }

    @Test
    @DisplayName("마이페이지 조회 성공 케이스 - KIM HYEJIN")
    void getMyPageSuccess() {
        // given
        Long ownerId = 1L;

        // when
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        OwnerMyPageRespDto response = ownerService.getMyPage(ownerId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("KIM HYEJIN");
    }

    @Test
    @DisplayName("마이페이지 수정 성공 케이스 - KIM HYEJIN")
    void updateMyPageSuccess() throws Exception {
        // given
        Long ownerId = 1L;
        OwnerMyPageUpdateReqDto reqDto = new OwnerMyPageUpdateReqDto("newkim@example.com", "New KIM HYEJIN", "01087654321", "Password123", "NewPassword123");

        // when
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        ownerService.updateMyPage(ownerId, reqDto);

        // then
        verify(ownerRepository, times(1)).findById(ownerId);
    }

    @Test
    @DisplayName("회원 탈퇴 성공 케이스 - KIM HYEJIN")
    void deleteOwnerSuccess() {
        // given
        Long ownerId = 1L;
        OwnerDeleteReqDto reqDto = new OwnerDeleteReqDto("Password123");

        // when
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        OwnerDeleteRespDto response = ownerService.deleteOwner(ownerId, reqDto);

        // then
        assertThat(response).isEqualTo(OwnerDeleteRespDto.INSTANCE);
        verify(ownerRepository, times(1)).save(owner);
    }
}