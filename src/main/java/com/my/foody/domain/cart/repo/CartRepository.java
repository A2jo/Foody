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

  Optional<Cart> findByIdAndUser(Long cartId, User user);

  Optional<Cart> findByUser(User user);

  @Query("select c from Cart c left join fetch c.store s where c.user = :user")
  Optional<Cart> findByUserWithStore(@Param(value = "user") User user);
}
