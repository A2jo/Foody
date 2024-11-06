package com.my.foody.domain.cartMenu;

import com.my.foody.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {
    List<CartMenu> findByCart(Cart cart);
}
