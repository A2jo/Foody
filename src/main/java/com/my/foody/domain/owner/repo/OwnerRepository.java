package com.my.foody.domain.owner.repo;

import com.my.foody.domain.owner.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsByEmail(String email);

    Optional<Owner> findByEmail(String email);
}