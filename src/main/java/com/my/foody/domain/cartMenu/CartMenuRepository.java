package com.my.foody.domain.cartMenu;

import com.my.foody.domain.cart.entity.Cart;
import com.my.foody.domain.cartMenu.dto.CartMenuDetailProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {

    @Query("select cm from CartMenu cm left join fetch cm.menu m where cm.cart = :cart")
    List<CartMenu> findByCartWithMenu(@Param(value = "cart")Cart cart);

    @Modifying
    void deleteByCart(Cart cart);

    @Query("select cm.quantity as quantity, " +
            "m.price as price, " +
            "m.name as menuName " +
            "from CartMenu cm " +
            "join Menu m on m.id = cm.menu.id")
    Page<CartMenuDetailProjectionDto> findCartMenuDetailByCart(@Param(value = "cart")Cart cart, Pageable pageable);
}
