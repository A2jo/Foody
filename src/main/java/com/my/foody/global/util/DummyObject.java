package com.my.foody.global.util;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.user.entity.User;

public class DummyObject {

    protected User newUser(Long id){
        String password = "Maeda1234!";
        return User.builder()
                .email("user1234@naver.com")
                .id(id)
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("userA")
                .nickname("userrr")
                .build();
    }

    protected User mockUser(){
        String password = "Maeda1234!";
        return User.builder()
                .email("user1234@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("userA")
                .nickname("userrr")
                .build();
    }


    protected Address mockAddress(User user){
        return Address.builder()
                .user(user)
                .roadAddress("도로명주소")
                .detailedAddress("상세주소")
                .build();
    }
}
