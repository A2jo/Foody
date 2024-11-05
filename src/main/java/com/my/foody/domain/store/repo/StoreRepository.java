package com.my.foody.domain.store.repo;

import com.my.foody.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
}
