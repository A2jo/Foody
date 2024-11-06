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

    Page<Menu> findAllByStoreId(Long storeId, Pageable pageable);

    Optional<Menu> findByIdAndIsDeletedFalse(Long menuId);


}
