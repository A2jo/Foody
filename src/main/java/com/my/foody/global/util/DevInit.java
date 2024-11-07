package com.my.foody.global.util;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.address.repo.AddressRepository;
import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;
import com.my.foody.domain.menu.entity.Menu;
import com.my.foody.domain.menu.repo.MenuRepository;
import com.my.foody.domain.owner.entity.Owner;
import com.my.foody.domain.owner.repo.OwnerRepository;
import com.my.foody.domain.review.entity.Review;
import com.my.foody.domain.review.repo.ReviewRepository;
import com.my.foody.domain.store.entity.Store;
import com.my.foody.domain.store.repo.StoreRepository;
import com.my.foody.domain.storeCategory.entity.StoreCategory;
import com.my.foody.domain.storeCategory.repo.StoreCategoryRepository;
import com.my.foody.domain.user.entity.User;
import com.my.foody.domain.user.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DevInit extends DummyObject{

    @Profile("demo")
    @Bean
    CommandLineRunner init(UserRepository userRepository, ReviewRepository reviewRepository, StoreRepository storeRepository, OwnerRepository ownerRepository,
                           StoreCategoryRepository storeCategoryRepository, CategoryRepository categoryRepository,
                           MenuRepository menuRepository, AddressRepository addressRepository){
        return (args) -> {

            User user = mockUser();
            userRepository.save(user);
            Address address = mainAddress(user);
            addressRepository.save(address);

            Owner owner = mockOwner();
            ownerRepository.save(owner);

            User reviewUser = newReviewAnotherUser();
            userRepository.save(reviewUser);
            Address address1 = mainAddress(reviewUser);
            addressRepository.save(address1);

            User reviewUser2 = newReviewUser();
            userRepository.save(reviewUser2);
            Address address2 = mainAddress(reviewUser2);
            addressRepository.save(address2);




            Store store = newStore(owner, "너무맛있는가게");
            Store store1 = newStore(owner, "진짜맛있는가게");
            Store store2 = newStore(owner, "피자스쿨");
            storeRepository.save(store);
            storeRepository.save(store1);
            storeRepository.save(store2);

            //메뉴
            Menu menu = newMenu(store1, "치킨");
            Menu menu1 = newMenu(store1, "피자");
            Menu menu2 = newMenu(store1, "떡볶이");
            menuRepository.save(menu);
            menuRepository.save(menu1);
            menuRepository.save(menu2);

            //Order


            //OrderMenu
            //newOrderMenu(menu1, )


            Category category = categoryRepository.findById(1L).get();
            StoreCategory storeCategory = newStoreCategory(category, store);
            storeCategoryRepository.save(storeCategory);


            Review review1 = newReview(store1, reviewUser);
            Review review2 = newReview(store1, reviewUser2);
            reviewRepository.save(review1);
            reviewRepository.save(review2);

        };
        }
}
