package com.my.foody.domain.user.service;

import com.my.foody.domain.address.dto.req.AddressCreateReqDto;
import com.my.foody.domain.address.dto.req.AddressModifyReqDto;
import com.my.foody.domain.address.dto.resp.AddressCreateRespDto;
import com.my.foody.domain.address.dto.resp.AddressModifyRespDto;
import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.user.dto.req.UserInfoModifyReqDto;
import com.my.foody.domain.user.dto.req.UserLoginReqDto;
import com.my.foody.domain.user.dto.req.UserSignUpReqDto;
import com.my.foody.domain.user.dto.resp.UserInfoModifyRespDto;
import com.my.foody.domain.address.service.AddressService;
import com.my.foody.domain.user.dto.resp.AddressDeleteRespDto;
import com.my.foody.domain.user.dto.resp.UserInfoRespDto;
import com.my.foody.domain.user.dto.resp.UserLoginRespDto;
import com.my.foody.domain.user.dto.resp.UserSignUpRespDto;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import com.my.foody.global.ex.BusinessException;
import com.my.foody.global.ex.ErrorCode;
import com.my.foody.global.jwt.JwtProvider;
import com.my.foody.global.jwt.TokenSubject;
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
    private final AddressRepository addressRepository;
    private final AddressService addressService;
    private final JwtProvider jwtProvider;

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


    public UserLoginRespDto login(UserLoginReqDto userLoginReqDto){
        User user = userRepository.findByEmail(userLoginReqDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.matchPassword(userLoginReqDto.getPassword());
        String token = jwtProvider.create(TokenSubject.of(user));
        return new UserLoginRespDto(token);
    }


    public UserInfoRespDto getUserInfo(Long userId) {
        User user = findByIdOrFail(userId);
        return new UserInfoRespDto(user);
    }


    public AddressCreateRespDto registerAddress(AddressCreateReqDto addressCreateReqDto, Long userId) {
        User user = findByIdOrFail(userId);
        Address address = addressCreateReqDto.toEntity(user);
        addressRepository.save(address);
        return new AddressCreateRespDto();
    }


    public User findByIdOrFail(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }


    @Transactional
    public UserInfoModifyRespDto modifyUserInfo(UserInfoModifyReqDto userInfoModifyReqDto, Long userId) {
        User user = findByIdOrFail(userId);
        validateDuplicatedUserInfo(userInfoModifyReqDto);
        user.modifyBasicInfo(userInfoModifyReqDto.getName(), userInfoModifyReqDto.getNickname(),
                userInfoModifyReqDto.getContact(), userInfoModifyReqDto.getEmail());
        return new UserInfoModifyRespDto();
    }


    private void validateDuplicatedUserInfo(UserInfoModifyReqDto userInfoModifyReqDto) {
        //이메일 중복 검증
        if (userInfoModifyReqDto.getEmail() != null && userRepository.existsByEmail(userInfoModifyReqDto.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        //닉네임 중복 검증
        if (userInfoModifyReqDto.getNickname() != null && userRepository.existsByNickname(userInfoModifyReqDto.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    public AddressModifyRespDto modifyAddress(AddressModifyReqDto addressModifyReqDto, Long userId, Long addressId) {
        User user = findByIdOrFail(userId);
        Address address = addressService.findByIdOrFail(addressId);
        address.validateUser(user);
        address.modifyAll(addressModifyReqDto.getRoadAddress(), addressModifyReqDto.getDetailedAddress());
        return new AddressModifyRespDto();
    }


    @Transactional
    public AddressDeleteRespDto deleteAddressById(Long addressId, Long userId) {
        User user = findByIdOrFail(userId);
        Address address = addressService.findByIdOrFail(addressId);
        address.validateUser(user);
        addressRepository.delete(address);
        return new AddressDeleteRespDto();
    }
}
