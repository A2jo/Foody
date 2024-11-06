package com.my.foody.domain.menu.repo;

import com.my.foody.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("select m from Menu m where m.id = :menuId and m.isDeleted = false")
    Optional<Menu> findActivateMenu(Long menuId);

    Optional<Menu> findByIdAndIsDeletedFalse(Long menuId);

}
