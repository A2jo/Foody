package com.my.foody.domain.cartMenu;

import com.my.foody.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {
    @Query("select cm from CartMenu cm left join fetch cm.cart c where cm.cart = :cart")
    List<CartMenu> findByCart(@Param(value = "cart") Cart cart);

    @Query("select cm from CartMenu cm left join fetch cm.menu m where cm.cart = :cart")
    List<CartMenu> findByCartWithMenu(@Param(value = "cart")Cart cart);

    @Modifying
    void deleteByCart(Cart cart);
}
