package com.my.foody.global.util;

import com.my.foody.domain.user.entity.User;

public class DummyObject {

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
}
