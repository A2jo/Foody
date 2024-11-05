package com.my.foody.global.util;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.review.entity.Review;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;

public class DummyObject {

    protected Store mockStore(){
        return Store.builder()
                .owner(null)
                .name("맛있는가게")
                .id(1L)
                .build();
    }

    protected Review newReview(Store store, User user){
        return Review.builder()
                .store(store)
                .user(user)
                .comment("너무맛있어용")
                .rating(3)
                .build();
    }

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
