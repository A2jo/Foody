package com.my.foody.domain.cart.repo;

import com.my.foody.domain.cart.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Page<Cart> findByUserId(Long userId, Pageable pageable);
    Optional<Cart> findByIdAndUserIdAndStoreId(Long id, Long userId, Long storeId);

}
