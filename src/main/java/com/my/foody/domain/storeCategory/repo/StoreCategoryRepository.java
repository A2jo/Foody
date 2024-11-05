package com.my.foody.domain.storeCategory.repo;

import com.my.foody.domain.storeCategory.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
    List<StoreCategory> findByCategoryId(Long categoryId);
}
