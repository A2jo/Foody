package com.my.foody.global.util;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.orderMenu.entity.OrderMenu;
import com.my.foody.domain.review.entity.Review;
import com.my.foody.domain.order.entity.Order;
import com.my.foody.domain.owner.entity.OrderStatus;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.storeCategory.entity.StoreCategory;
import com.my.foody.domain.user.entity.User;
import io.lettuce.core.ScoredValueScanCursor;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DummyObject {

    protected Order newOrder(Store store, User user, Address address){
        return Order.builder()
                .orderStatus(OrderStatus.DELIVERED)
                .user(user)
                .store(store)
                .address(address)
                .totalAmount(100000L)
                .build();
    }

    protected OrderMenu newOrderMenu(Menu menu, Order order){
        return OrderMenu.builder()
                .menuId(menu.getId())
                .order(order)
                .quantity(1L)
                .price(menu.getPrice())
                .build();
    }

    protected Menu newMenu(Store store, String menuName){
        return Menu.builder()
                .store(store)
                .price(100000L)
                .name(menuName)
                .isDeleted(false)
                .isSoldOut(false)
                .build();
    }

    protected Store mockStore(){
        return Store.builder()
                .owner(null)
                .name("맛있는가게")
                .openTime(LocalTime.now())
                .endTime(LocalTime.now())
//                .id(1L)
                .contact("010-1234-2342")
                .isDeleted(false)
                .minOrderAmount(1000L)
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
                .email("user11111234@naver.com")
                .id(id)
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("떡볶이")
                .nickname("절대중복되지않는닉네임..")
                .isDeleted(false)
                .build();
    }

    protected User newReviewAnotherUser(){
        String password = "Maeda1234!";
        return User.builder()
                .email("user11111234@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("떡볶이")
                .nickname("절대중복되지않는닉네임..")
                .isDeleted(false)
                .build();
    }

    protected User newReviewUser(){
        String password = "Maeda1234!";
        return User.builder()
                .email("test1234564@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("유저CCCC")
                .nickname("맛도리")
                .isDeleted(false)
                .build();
    }

    protected Address mainAddress(User user){
        return Address.builder()
                .detailedAddress("상세 주소")
                .roadAddress("도로명 주소")
                .isMain(true)
                .user(user)
                .build();
    }


    protected User mockUser(){
        String password = "Maeda1234!";
        return User.builder()
                .email("user1234@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("userA")
                .nickname("마에다")
                .isDeleted(false)
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

    protected Owner mockOwner(){
        String password = "password123!";
        return Owner.builder()
                .email("ownertest@naver.com")
                .password(PasswordEncoder.encode(password))
                .contact("010-1234-5678")
                .name("ownerA")
                .isDeleted(false)
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
                .isDeleted(false)
                .build();
    }

    //가게
    protected Store mockStore(Owner owner) {
        return Store.builder()
                .name("맛있는 가게")
                .owner(owner)
                .description("맛있어요")
                .contact("010-1234-5678")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)
                .build();
    }


    protected Store newStore(Owner owner, String name, Long id) {
        return Store.builder()
                .name(name)
                .owner(owner)
                .id(id)
                .description("맛있어요")
                .contact("010-1234-5678")
                .minOrderAmount(10000L)
                .openTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .isDeleted(false)
                .build();
    }
    protected StoreCategory newStoreCategory(Category category, Store store){
        return StoreCategory.builder()
                .category(category)
                .store(store)
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
