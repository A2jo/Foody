package com.my.foody.domain.owner.repo;

import com.my.foody.domain.owner.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsByEmail(String email);

    @Query("select o from Owner o where o.id = :ownerId and o.isDeleted = false")
    Optional<Owner> findActivateOwner(@Param(value = "ownerId") Long ownerId);


    Optional<Owner> findByEmail(String email);
}