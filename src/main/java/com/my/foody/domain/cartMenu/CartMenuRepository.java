package com.my.foody.domain.cartMenu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {
}
