package com.my.foody.global.util;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.user.entity.User;

import java.time.LocalTime;

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
    
    //사장
    protected Owner mockOwner(Long id){
        String password = "password123!";
        return Owner.builder()
                .id(id)
                .email("ownertest@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("ownerA")
                .build();
    }


    //id 가진 user
    protected User newUser(Long id){
        String password = "Maeda1234!";
        return User.builder()
                .id(id)
                .email("user1234@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("userA")
                .nickname("userrr")
                .build();
    }


    //사장
    protected Owner newOwner(Long id){
        String password = "password123!";
        return Owner.builder()
                .id(id)
                .email("ownertest@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("ownerA")
                .build();
    }

    //가게
    protected Store mockStore(Owner owner) {
        return Store.builder()
                .name("가게이름")
                .owner(owner)
                .description("가게설명")
                .contact("010-1234-5678")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)
                .build();
    }

    //주문
    protected Order mockOrder(Owner owner, User user, Address address) {
        Store store = mockStore(owner);
        return Order.builder()
                .orderStatus(OrderStatus.PENDING)
                .user(user)
                .store(store)
                .address(address)
                .totalAmount(10000L)
                .build();
    }

}
