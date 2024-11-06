package com.my.foody.domain.menu.repo;

import com.my.foody.domain.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("select m from Menu m where m.id = :menuId and m.isDeleted = false")
    Optional<Menu> findActivateMenu(Long menuId);

    Optional<Menu> findByIdAndIsDeletedFalse(Long menuId);

    Page<Menu> findByStoreId(Long storeId, Pageable pageable);

    @Query("SELECT m.id AS id, m.name AS name, m.price AS price, m.isDeleted AS isDeleted, m.isSoldOut AS isSoldOut " +
            "FROM Menu m WHERE m.store.id = :storeId AND m.isDeleted = false AND m.isSoldOut = false")
    Page<MenuProjection> findMenusByStoreId(Long storeId, Pageable pageable);
}
