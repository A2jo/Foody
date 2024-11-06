package com.my.foody.domain.storeCategory.repo;

import com.my.foody.domain.storeCategory.entity.StoreCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
    Page<StoreCategory> findByCategoryId(Long categoryId, Pageable pageable);

    Optional<StoreCategory> findByCategoryIdAndStoreId(Long categoryId, Long storeId);
}
