package com.my.foody.domain.store.repo;

import com.my.foody.domain.store.entity.Store;
import org.springframework.data.domain.Pageable;
import com.my.foody.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);

    Long countByOwnerId(Long ownerId);

    List<Store> findByOwnerId(Long ownerId);

    Long countByOwnerIdAndIsDeletedFalse(Long ownerId);

    @Query("select s from Store s where s.id = :storeId and s.isDeleted = false")
    Optional<Store> findActivateStore(@Param(value = "storeId")Long storeId);

    // 키워드와 삭제 여부를 기준으로 상점 검색 (페이징 지원)
    @Query("SELECT s FROM Store s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND s.isDeleted = false")
    List<Store> findByNameContainingIgnoreCaseAndIsDeletedFalse(@Param("keyword") String keyword, Pageable pageable);
}
