package com.my.foody.domain.menu.repo;

import com.my.foody.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByIdAndIsDeletedFalse(Long menuId);
}
