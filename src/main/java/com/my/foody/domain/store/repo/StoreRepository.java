package com.my.foody.domain.store.repo;

import com.my.foody.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);

    Long countByOwnerId(Long ownerId);

    List<Store> findByOwnerId(Long ownerId);
}
