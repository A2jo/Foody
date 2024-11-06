package com.my.foody.domain.cart.repo;

import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
  Page<Cart> findByUserId(Long userId, Pageable pageable);
  @Query("SELECT c FROM Cart c WHERE c.id = :cartId AND c.user.id = :userId AND c.store.id = :storeId")
  Optional<Cart> findWithStoreAndMenuByIdAndUserIdAndStoreId(@Param("cartId") Long cartId, @Param("userId") Long userId, @Param("storeId") Long storeId);
  Optional<Cart> findByUser(User user);
}
