package com.my.foody.domain.storeCategory.repo;

import com.my.foody.domain.storeCategory.entity.StoreCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

import java.util.Optional;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
    Page<StoreCategory> findByCategoryId(Long categoryId, Pageable pageable);

    Optional<StoreCategory> findByCategoryIdAndStoreId(Long categoryId, Long storeId);
    List<StoreCategory> findByCategoryId(Long categoryId);
    Page<StoreCategory> findByCategoryId(Long categoryId, Pageable pageable);

    Optional<StoreCategory> findByCategoryIdAndStoreId(Long categoryId, Long storeId);
    void deleteByStoreId(Long storeId);
    @Query("SELECT sc.store.id AS storeId, sc.store.name AS storeName, sc.category.id AS categoryId, sc.store.minOrderAmount AS minOrderAmount " +
                  "FROM StoreCategory sc WHERE sc.category.id = :categoryId")
<<<<<<< HEAD
    Page<StoreCategoryProjection> findStoresByCategoryId(Long categoryId, Pageable pageable);
    @Query("SELECT sc.store.id AS storeId, sc.store.name AS storeName, sc.category.id AS categoryId, sc.store.minOrderAmount AS minOrderAmount " +
                  "FROM StoreCategory sc WHERE sc.category.id = :categoryId")
    Page<StoreCategoryProjection> findStoresByCategoryId(Long categoryId, Pageable pageable);
    List<StoreCategory> findByCategoryId(Long categoryId);
    List<StoreCategory> findByCategoryId(Long categoryId);
    Page<StoreCategory> findByCategoryId(Long categoryId, Pageable pageable);
    @Query("SELECT sc.store.id AS storeId, sc.store.name AS storeName, sc.category.id AS categoryId " +
            "FROM StoreCategory sc WHERE sc.category.id = :categoryId")
    @Query("SELECT sc.store.id AS storeId, sc.store.name AS storeName, sc.category.id AS categoryId, sc.store.minOrderAmount AS minOrderAmount " +
                  "FROM StoreCategory sc WHERE sc.category.id = :categoryId")
    Page<StoreCategoryProjection> findStoresByCategoryId(Long categoryId, Pageable pageable);
=======
    Page<StoreCategoryProjection> findStoresByCategoryId(@Param(value = "categoryId")Long categoryId, Pageable pageable);

>>>>>>> 3dd649c4c1bb980fe39dabe6cec562d89365fba6
}
